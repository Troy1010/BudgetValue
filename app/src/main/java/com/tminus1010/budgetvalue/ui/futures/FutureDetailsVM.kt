package com.tminus1010.budgetvalue.ui.futures

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.NoDescriptionEnteredException
import com.tminus1010.budgetvalue.all_layers.extensions.*
import com.tminus1010.budgetvalue.app.CategoryParser
import com.tminus1010.budgetvalue.app.CategorizeMatchingTransactions
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.*
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.ui.all_features.model.SearchType
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class FutureDetailsVM @Inject constructor(
    private val categoryParser: CategoryParser,
    private val selectedCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val futuresRepo: FuturesRepo,
    private val showToast: ShowToast,
    private val categorizeMatchingTransactions: CategorizeMatchingTransactions,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
    // # Setup
    val future = MutableSharedFlow<Future>(1)

    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        try {
            val futureToPush =
                Future(
                    name = name.value ?: throw NoDescriptionEnteredException(),
                    categoryAmountFormulas = categoryAmountFormulas.value,
                    fillCategory = fillCategory.value!!,
                    terminationStrategy = if (isOnlyOnce.value) TerminationStrategy.ONCE else TerminationStrategy.PERMANENT,
                    terminationDate = null,
                    isAvailableForManual = true,
                    onImportTransactionMatcher = when (searchType.value) {
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
                    categorizeMatchingTransactions({ futureToPush.onImportTransactionMatcher?.isMatch(it) ?: false }, futureToPush::categorize)
                        .also { showToast(NativeText.Simple("$it transactions categorized")) }
                if (futureToPush.name != future.value!!.name) futuresRepo.delete(future.value!!)
                userTryNavUp()
            }
        } catch (e: Throwable) {
            when (e) {
                is NoDescriptionEnteredException -> showToast(NativeText.Simple("Fill description or use another search type"))
                else -> throw e
            }
        }
    }

    private val userSetTotalGuess = MutableSharedFlow<BigDecimal>()
    fun userSetTotalGuess(s: String) {
        userSetTotalGuess.onNext(s.toMoneyBigDecimal())
    }

    private val userSetIsOnlyOnce = MutableSharedFlow<Boolean>()
    fun userSetIsOnlyOnce(b: Boolean) {
        userSetIsOnlyOnce.onNext(b)
    }

    private val userSetSearchType = MutableSharedFlow<SearchType>()
    fun userSetSearchType(searchType: SearchType) {
        userSetSearchType.onNext(searchType)
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
        userSetFillCategory.onNext(categoryParser.parseCategory(categoryName))
    }

    private val userSetName = MutableSharedFlow<String>()
    fun userSetName(s: String) {
        userSetName.onNext(s)
    }

    fun userTryNavToSetSearchTexts() {
        navToSetSearchTexts.onNext()
    }

    fun userDeleteFutureOrReplay() {
        runBlocking { futuresRepo.delete(future.value!!) }
        userTryNavUp()
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesSharedVM.clearSelection() }
        navUp.onNext()
    }

    // # Internal
    private val name =
        future
            .map { it.name }
            .flatMapLatest { userSetName.onStart { emit(it) } }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    private val totalGuess =
        future
            .map { it.totalGuess }
            .flatMapLatest { userSetTotalGuess.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal("-10"))
    private val isOnlyOnce =
        future
            .map { it.terminationStrategy == TerminationStrategy.PERMANENT }
            .flatMapLatest { userSetIsOnlyOnce.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val searchType =
        future
            .map {
                when (it.onImportTransactionMatcher) {
                    is TransactionMatcher.SearchText,
                    -> SearchType.DESCRIPTION
                    is TransactionMatcher.ByValue,
                    -> SearchType.TOTAL
                    is TransactionMatcher.Multi,
                    -> if (it.onImportTransactionMatcher.transactionMatchers.all { it is TransactionMatcher.SearchText })
                        SearchType.DESCRIPTION
                    else
                        SearchType.DESCRIPTION_AND_TOTAL
                    null,
                    -> SearchType.NONE
                }
            }
            .flatMapLatest { userSetSearchType.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SearchType.DESCRIPTION)
    private val categoryAmountFormulas =
        future
            .map { it.categoryAmountFormulas }
            .flatMapLatest { oldCategoryAmountFormulas ->
                combine(userCategoryAmountFormulas.flow, selectedCategoriesSharedVM.selectedCategories)
                { userCategoryAmountFormulas, selectedCategories ->
                    CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                        .plus(oldCategoryAmountFormulas)
                        .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())

    // I might want to change this requirement
    private val fillCategory =
        future
            .map { it.fillCategory }
            .flatMapLatest {
                selectedCategoriesSharedVM.selectedCategories.drop(1)
                    .flatMapLatest { selectedCategories ->
                        userSetFillCategory
                            .onStart { emit(selectedCategories.find { it.defaultAmountFormula.isZero() } ?: selectedCategories.getOrNull(0)) }
                    }
                    .onStart { emit(it) }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            fillCategory
                ?.let { categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory] }
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value(BigDecimal.ZERO))

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
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Name"),
                        EditTextVMItem(text = name.value!!, onDone = { userSetName(it) }),
                    ),
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(SearchType.values(), searchType, onNewItem = { userSetSearchType(it) }),
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
                        MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = { userSetTotalGuess(it) }),
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
                        CheckboxVMItem(isOnlyOnce.value, onCheckChanged = ::userSetIsOnlyOnce),
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
                    title = "Delete",
                    onClick = ::userDeleteFutureOrReplay,
                ),
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