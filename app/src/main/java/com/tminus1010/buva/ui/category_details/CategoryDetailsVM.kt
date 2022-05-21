package com.tminus1010.buva.ui.category_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.InvalidCategoryNameException
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.DeleteCategoryFromActiveDomain
import com.tminus1010.buva.app.ReplaceCategoryGlobally
import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.model.SearchType
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deleteCategoryFromActiveDomain: DeleteCategoryFromActiveDomain,
    private val categoriesRepo: CategoriesRepo,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
) : ViewModel() {
    // # Internal
    private val originalCategory = savedStateHandle.get<Category>(KEY1)
    private val category = savedStateHandle.getLiveData<Category>(KEY1)

    // # User Intents
    fun userSetCategoryName(s: String) {
        category.value = category.value!!.copy(name = s)
    }

    fun userSetCategoryDefaultAmountFormula(amountFormula: AmountFormula) {
        category.value = category.value!!.copy(defaultAmountFormula = amountFormula)
    }

    fun userSetCategoryType(categoryType: CategoryType) {
        category.value = category.value!!.copy(type = categoryType)
    }

    fun userDeleteCategory() {
        errors.globalScope.launch {
            deleteCategoryFromActiveDomain(category.value!!)
        }.use(throbberSharedVM)
        navUp.easyEmit(Unit)
    }

    fun userSubmit() {
        viewModelScope.launch(errors) {
            if (category.value!!.name == "" || category.value!!.name == "<NAME>"
                || category.value!!.name.equals(Category.DEFAULT.name, ignoreCase = true)
                || category.value!!.name.equals(Category.UNRECOGNIZED.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
            if (originalCategory != null && originalCategory != category.value)
                replaceCategoryGlobally(originalCategory, category.value!!)
            else
                categoriesRepo.push(category.value!!)
            navUp.easyEmit(Unit)
        }
    }

    fun userSetSearchType(searchType: SearchType) {
        val onImportTransactionMatcher =
            when (searchType) {
                SearchType.DESCRIPTION -> TransactionMatcher.Multi()
                SearchType.DESCRIPTION_AND_TOTAL -> TransactionMatcher.Multi()
                SearchType.TOTAL -> TransactionMatcher.ByValue(totalGuess.value)
                SearchType.NONE -> null
            }
        category.value = category.value!!.copy(onImportTransactionMatcher = onImportTransactionMatcher)
    }

    private val totalGuess = MutableStateFlow(BigDecimal("-10"))
    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    fun userSetIsRememberedByDefault(b: Boolean) {
        category.value = category.value!!.copy(isRememberedByDefault = b)
    }

    fun userAddSearchText() {
        category.value = category.value!!.copy(onImportTransactionMatcher = category.value!!.onImportTransactionMatcher.withSearchText(""))
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val showDeleteConfirmationPopup = MutableSharedFlow<String>()

    // # State
    val title = flowOf("Category").shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val optionsTableView =
        category.map { category ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextVMItem("Name"),
                        EditTextVMItem(text = category.name, onDone = ::userSetCategoryName),
                    ),
                    listOf(
                        TextVMItem("Default Amount"),
                        AmountFormulaPresentationModel1(
                            amountFormula = this.category.map { it.defaultAmountFormula },
                            onNewAmountFormula = ::userSetCategoryDefaultAmountFormula
                        ),
                    ),
                    listOf(
                        TextVMItem("Type"),
                        SpinnerVMItem(values = CategoryType.getPickableValues().toTypedArray(), initialValue = category.type, onNewItem = ::userSetCategoryType),
                    ),
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(
                            values = SearchType.values(),
                            initialValue = transactionMatcherPresentationFactory.searchType(category.onImportTransactionMatcher),
                            onNewItem = ::userSetSearchType,
                        ),
                    ),
                    if (sequenceOf(SearchType.TOTAL, SearchType.DESCRIPTION_AND_TOTAL).any { it == transactionMatcherPresentationFactory.searchType(category.onImportTransactionMatcher) })
                        listOf(
                            TextPresentationModel(
                                style = TextPresentationModel.Style.TWO,
                                text1 = transactionMatcherPresentationFactory.totalTitle(category.onImportTransactionMatcher)
                            ),
                            MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = ::userSetTotalGuess),
                        ) else null,
                    listOf(
                        TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Is Remembered By Default"),
                        CheckboxVMItem(initialValue = category.isRememberedByDefault, onCheckChanged = ::userSetIsRememberedByDefault),
                    ),
                    *when (category.onImportTransactionMatcher) {
                        is TransactionMatcher.ByValue ->
                            listOf(
                                listOf(
                                    TextVMItem("Search Total"),
                                    TextVMItem(
                                        text1 = category.onImportTransactionMatcher.searchTotal.toString()
                                    )
                                )
                            )
                        is TransactionMatcher.SearchText ->
                            listOf(
                                listOf(
                                    TextVMItem("Search Text"),
                                    TextVMItem(
                                        text1 = category.onImportTransactionMatcher.searchText
                                    )
                                )
                            )
                        is TransactionMatcher.Multi ->
                            category.onImportTransactionMatcher.transactionMatchers.map {
                                listOf<IHasToViewItemRecipe>(
                                    TextVMItem("Search Text"),
                                    TextVMItem(
                                        text1 = it.toString()
                                    )
                                )
                            }
                                .plus(
                                    listOf(
                                        listOf(
                                            TextVMItem(""),
                                            ButtonVMItem(
                                                title = "Add Another Search Text",
                                                onClick = { userAddSearchText() },
                                            ),
                                        )
                                    )
                                )
                        else ->
                            listOf(listOf())
                    }.toTypedArray()
//                        category.onImportTransactionMatcher.
//                        *sourceList.withIndex().map { (i, s) ->
//                            listOf<IHasToViewItemRecipe>(
//                                EditTextVMItem(
//                                    text = s,
//                                    onDone = { sourceList[i] = it },
//                                    menuVMItems = MenuVMItems(
//                                        MenuVMItem(
//                                            title = "Delete",
//                                            onClick = { sourceList.removeAt(i) }
//                                        ),
//                                        MenuVMItem(
//                                            title = "Copy from Transactions",
//                                            onClick = { navToChooseTransaction.onNext(i) }
//                                        ),
//                                    )
//                                )
//                            )
//                        }.toTypedArray(),
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }

    //            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Delete",
                    onClick = { showDeleteConfirmationPopup.easyEmit(category.value!!.name) }
                ),
                ButtonVMItem(
                    title = "Done",
                    onClick = ::userSubmit
                ),
            )
        )
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}