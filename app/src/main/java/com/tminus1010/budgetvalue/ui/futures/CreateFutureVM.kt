package com.tminus1010.budgetvalue.ui.futures

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.SearchType
import com.tminus1010.budgetvalue.all_layers.NoDescriptionEnteredException
import com.tminus1010.budgetvalue.all_layers.extensions.*
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.app.CategorizeMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.*
import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.framework.android.ShowAlertDialog
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFutureVM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectedCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val futuresRepo: FuturesRepo,
    private val showToast: ShowToast,
    private val categorizeMatchingUncategorizedTransactions: CategorizeMatchingUncategorizedTransactions,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
    private val transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # Setup
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        GlobalScope.launch {
            showAlertDialog.value!!(
                body = NativeText.Simple("What would you like to name this future?"),
                initialText = categoryAmountFormulas.value
                    .map { (category, amountFormula) ->
                        if (category != fillCategory.value)
                            amountFormula.toDisplayStr2() + " " + category.name
                        else
                            category.name
                    }
                    .joinToString(", "),
                onSubmitText = {
                    try {
                        val futureToPush =
                            Future(
                                name = it.toString(),
                                categoryAmountFormulas = categoryAmountFormulas.value,
                                fillCategory = fillCategory.value!!,
                                terminationStrategy = if (userSetIsOnlyOnce.value) TerminationStrategy.ONCE else TerminationStrategy.PERMANENT,
                                terminationDate = null,
                                isAvailableForManual = true,
                                onImportMatcher = when (searchType.value) {
                                    SearchType.DESCRIPTION -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) })
                                    SearchType.DESCRIPTION_AND_TOTAL -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) }.plus(TransactionMatcher.ByValue(totalGuess.value)))
                                    SearchType.TOTAL -> TransactionMatcher.ByValue(totalGuess.value)
                                    SearchType.NONE -> null
                                },
                                totalGuess = totalGuess.value,
                            )
                        runBlocking {
                            futuresRepo.push(futureToPush)
                            if (futureToPush.terminationStrategy == TerminationStrategy.PERMANENT)
                                categorizeMatchingUncategorizedTransactions({ futureToPush.onImportMatcher?.isMatch(it) ?: false }, futureToPush::categorize)
                                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
                            selectedCategoriesSharedVM.clearSelection()
                            navUp.emit(Unit)
                        }
                    } catch (e: Throwable) {
                        when (e) {
                            is NoDescriptionEnteredException -> showToast(NativeText.Simple("Fill description or use another search type"))
                            else -> throw e
                        }
                    }
                }
            )
        }
    }

    private val totalGuess = MutableStateFlow(transactionsInteractor.mostRecentUncategorizedSpend.value?.amount ?: BigDecimal("-10"))
    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    private val userSetIsOnlyOnce = MutableStateFlow(false)
    fun userSetIsOnlyOnce(b: Boolean) {
        userSetIsOnlyOnce.onNext(b)
    }

    private val searchType = MutableStateFlow(SearchType.DESCRIPTION)
    fun userSetSearchType(searchType: SearchType) {
        this.searchType.onNext(searchType)
    }

    private val userCategoryAmountFormulas = SourceHashMap<Category, AmountFormula>()
    fun userSetCategoryAmountFormula(category: Category, amountFormula: AmountFormula) {
        if (amountFormula.isZero())
            userCategoryAmountFormulas.remove(category)
        else
            userCategoryAmountFormulas[category] = amountFormula
    }

    private val userSetFillCategory = MutableSharedFlow<Category?>()
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoriesInteractor.parseCategory(categoryName))
    }

    fun userTryNavToSetSearchTexts() {
        navToSetSearchTexts.onNext()
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesSharedVM.clearSelection() }
        navUp.onNext()
    }

    // # Internal
    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesSharedVM.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())
    private val fillCategory =
        selectedCategoriesSharedVM.selectedCategories
            .flatMapLatest { selectedCategories ->
                userSetFillCategory
                    .onStart { emit(selectedCategories.find { it.defaultAmountFormula.isZero() } ?: selectedCategories.getOrNull(0)) }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            fillCategory
                ?.let { categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory] }
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value.ZERO)


    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToCategorySelection = MutableSharedFlow<Unit>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val otherInputTableView =
        searchType.map { searchType ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(SearchType.values(), searchType, onNewItem = ::userSetSearchType),
                    ),
                    listOf(
                        TextPresentationModel(
                            style = TextPresentationModel.Style.TWO,
                            text2 = this.searchType
                                .map {
                                    when (it) {
                                        SearchType.NONE,
                                        SearchType.DESCRIPTION,
                                        -> "Total Guess"
                                        SearchType.TOTAL,
                                        SearchType.DESCRIPTION_AND_TOTAL,
                                        -> "Exact Total"
                                    }
                                }
                        ),
                        MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = ::userSetTotalGuess),
                    ),
                    if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
                        listOf(
                            TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Texts"),
                            ButtonVMItem(
                                title = "View Search Texts",
                                onClick = { userTryNavToSetSearchTexts() },
                            ),
                        )
                    else null,
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Is Only Once"),
                        CheckboxVMItem(userSetIsOnlyOnce.value, onCheckChanged = ::userSetIsOnlyOnce),
                    ),
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
    val categoryAmountsTableView =
        combine(categoryAmountFormulas.flatMapSourceHashMap { it.itemFlowMap }, fillCategory)
        { categoryAmountFormulaItemFlows, fillCategory ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf<IHasToViewItemRecipe>(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                    ),
                    *categoryAmountFormulaItemFlows.map { (category, amountFormula) ->
                        CategoryAmountFormulaPresentationModel(category, fillCategory, if (category == fillCategory) fillAmountFormula else amountFormula, { userSetFillCategory(it.name) }, { userSetCategoryAmountFormula(category, it) }).toHasToViewItemRecipes()
                    }.toTypedArray(),
                ),
                dividerMap = categoryAmountFormulaItemFlows.keys.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 1 } // header row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Add Or Remove Categories",
                    onClick = ::userTryNavToCategorySelection,
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userTrySubmit,
                ),
            )
        )
}