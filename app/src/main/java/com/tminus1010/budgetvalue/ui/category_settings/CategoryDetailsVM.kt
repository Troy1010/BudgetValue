package com.tminus1010.budgetvalue.ui.category_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.InvalidCategoryNameException
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.app.ReplaceCategoryGlobally
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.AmountFormula
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryType
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.ui.all_features.ThrobberSharedVM
import com.tminus1010.budgetvalue.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.budgetvalue.ui.all_features.model.SearchType
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: CategoriesRepo,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
) : ViewModel() {
    // # View Events
    val originalCategory = MutableSharedFlow<Category?>(1)

    // # Internal
    private val newCategoryToPush = MutableSharedFlow<Category>()
    private val categoryToPush =
        merge(
            originalCategory.map { it ?: Category("") },
            newCategoryToPush,
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, Category(""))

    // # User Intents
    fun userSetCategoryName(s: String) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(name = s))
    }

    fun userSetCategoryDefaultAmountFormula(amountFormula: AmountFormula) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(defaultAmountFormula = amountFormula))
    }

    fun userSetCategoryType(categoryType: CategoryType) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(type = categoryType))
    }

    fun userDeleteCategory() {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            deleteCategoryFromActiveDomainUC(categoryToPush.value)
        })
        navUp.easyEmit(Unit)
    }

    fun userSubmit() {
        viewModelScope.launch(CoroutineExceptionHandler { _, e -> errors.easyEmit(e) }) {
            if (categoryToPush.value.name == "" ||
                categoryToPush.value.name.equals(Category.DEFAULT.name, ignoreCase = true) ||
                categoryToPush.value.name.equals(Category.UNRECOGNIZED.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
            if (originalCategory.value != null && originalCategory.value != categoryToPush.value)
                replaceCategoryGlobally(originalCategory.value!!, categoryToPush.value)
            else
                categoriesRepo.push(categoryToPush.value)
            navUp.easyEmit(Unit)
        }
    }

    fun userSetSearchType(searchType: SearchType) {
        val onImportTransactionMatcher =
            when (searchType) {
                SearchType.DESCRIPTION -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) })
                SearchType.DESCRIPTION_AND_TOTAL -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) }.plus(TransactionMatcher.ByValue(totalGuess.value)))
                SearchType.TOTAL -> TransactionMatcher.ByValue(totalGuess.value)
                SearchType.NONE -> null
            }
        newCategoryToPush.easyEmit(categoryToPush.value.copy(onImportTransactionMatcher = onImportTransactionMatcher))
    }

    private val totalGuess = MutableStateFlow(BigDecimal("-10"))
    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    fun userTryNavToSetSearchTexts() {
        navToSetSearchTexts.onNext()
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val showDeleteConfirmationPopup = MutableSharedFlow<String>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val title =
        originalCategory.flatMapConcat { if (it == null) flowOf("Create a new Category") else categoryToPush.map { "Settings (${it.name})" } }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val optionsTableView =
        categoryToPush.map { categoryToPush ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextVMItem("Name"),
                        EditTextVMItem2(text = categoryToPush.name, onDone = ::userSetCategoryName),
                    ),
                    listOf(
                        TextVMItem("Default Amount"),
                        AmountFormulaPresentationModel1(
                            amountFormula = this.categoryToPush.map { it.defaultAmountFormula }.stateIn(viewModelScope),
                            onNewAmountFormula = ::userSetCategoryDefaultAmountFormula
                        ),
                    ),
                    listOf(
                        TextVMItem("Type"),
                        SpinnerVMItem(values = CategoryType.getPickableValues().toTypedArray(), initialValue = categoryToPush.type, onNewItem = ::userSetCategoryType),
                    ),
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(
                            values = SearchType.values(),
                            initialValue = transactionMatcherPresentationFactory.searchType(categoryToPush.onImportTransactionMatcher),
                            onNewItem = ::userSetSearchType,
                        ),
                    ),
                    if (sequenceOf(SearchType.TOTAL, SearchType.DESCRIPTION_AND_TOTAL).any { it == transactionMatcherPresentationFactory.searchType(categoryToPush.onImportTransactionMatcher) })
                        listOf(
                            TextPresentationModel(
                                style = TextPresentationModel.Style.TWO,
                                text1 = transactionMatcherPresentationFactory.totalTitle(categoryToPush.onImportTransactionMatcher)
                            ),
                            MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = ::userSetTotalGuess),
                        ) else null,
                    if (transactionMatcherPresentationFactory.hasSearchTexts(categoryToPush.onImportTransactionMatcher))
                        listOf(
                            TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Search Texts"),
                            ButtonVMItem(title = "View Search Texts", onClick = ::userTryNavToSetSearchTexts),
                        )
                    else null,
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        originalCategory.map { originalCategory ->
            listOfNotNull(
                if (originalCategory == null)
                    null
                else
                    ButtonVMItem(
                        title = "Delete",
                        onClick = { showDeleteConfirmationPopup.easyEmit(originalCategory.name) }
                    ),
                ButtonVMItem(
                    title = "Done",
                    onClick = ::userSubmit
                ),
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}