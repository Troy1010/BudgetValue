package com.tminus1010.budgetvalue.ui.category_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.InvalidCategoryNameException
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.app.CategoriesInteractor
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
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: CategoriesRepo,
    private val categoriesInteractor: CategoriesInteractor,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
) : ViewModel() {
    // # View Events
    val originalCategoryName = MutableStateFlow("")
    val isForNewCategory = MutableStateFlow(true)

    // # Internal
    private val originalCategory = originalCategoryName.map { if (it == "") null else categoriesInteractor.parseCategory(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val newCategoryToPush = MutableSharedFlow<Category>()
    private val categoryToPush =
        merge(
            combine(isForNewCategory, originalCategoryName)
            { isForNewCategory, originalCategoryName ->
                if (isForNewCategory)
                    Category("")
                else
                    categoriesInteractor.parseCategory(originalCategoryName)
            },
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
            if (originalCategoryName.value != "" && originalCategoryName.value != categoryToPush.value.name)
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
    val title = isForNewCategory.flatMapConcat { if (it) flowOf("Create a new Category") else categoryToPush.map { "Settings (${it.name})" } }
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
    val buttons =
        isForNewCategory.map { isForNewCategory ->
            listOfNotNull(
                if (isForNewCategory)
                    null
                else
                    ButtonVMItem(
                        title = "Delete",
                        onClick = { showDeleteConfirmationPopup.easyEmit(originalCategoryName.value) }
                    ),
                ButtonVMItem(
                    title = "Done",
                    onClick = ::userSubmit
                ),
            )
        }
}