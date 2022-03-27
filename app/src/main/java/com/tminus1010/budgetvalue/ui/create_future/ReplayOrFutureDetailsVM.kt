package com.tminus1010.budgetvalue.ui.create_future

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.SearchType
import com.tminus1010.budgetvalue.all_layers.NoDescriptionEnteredException
import com.tminus1010.budgetvalue.all_layers.extensions.*
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.app.CategorizeMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.*
import com.tminus1010.budgetvalue.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.framework.view.Toaster
import com.tminus1010.budgetvalue.ui.all_features.model.*
import com.tminus1010.budgetvalue.ui.select_categories.SelectCategoriesModel
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReplayOrFutureDetailsVM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectedCategoriesModel: SelectCategoriesModel,
    private val futuresRepo: FuturesRepo,
    private val toaster: Toaster,
    private val categorizeMatchingUncategorizedTransactions: CategorizeMatchingUncategorizedTransactions,
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
                    terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.ONCE,
                    terminationDate = null,
                    isAvailableForManual = true,
                    onImportMatcher = when (searchType.value) {
                        SearchType.DESCRIPTION -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) })
                        SearchType.DESCRIPTION_AND_TOTAL -> TODO()
                        SearchType.TOTAL -> TransactionMatcher.ByValue(totalGuess.value)
                    },
                    totalGuess = totalGuess.value,
                )
            runBlocking {
                futuresRepo.push(futureToPush)
                if (futureToPush.terminationStrategy == TerminationStrategy.PERMANENT)
                    categorizeMatchingUncategorizedTransactions(futureToPush.onImportMatcher::isMatch, futureToPush::categorize)
                        .also { toaster.toast("$it transactions categorized") }
                if (futureToPush.name != future.value!!.name) futuresRepo.delete(future.value!!)
                userTryNavUp()
            }
        } catch (e: Throwable) {
            when (e) {
                is NoDescriptionEnteredException -> toaster.toast("Fill description or use another search type")
                else -> throw e
            }
        }
    }

    private val userSetTotalGuess = MutableSharedFlow<BigDecimal>()
    fun userSetTotalGuess(s: String) {
        userSetTotalGuess.onNext(s.toMoneyBigDecimal())
    }

    private val userSetIsPermanent = MutableSharedFlow<Boolean>()
    fun userSetIsPermanent(b: Boolean) {
        userSetIsPermanent.onNext(b)
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
        userSetFillCategory.onNext(categoriesInteractor.parseCategory(categoryName))
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
        runBlocking { selectedCategoriesModel.clearSelection() }
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
    private val isPermanent =
        future
            .map { it.terminationStrategy == TerminationStrategy.PERMANENT }
            .flatMapLatest { userSetIsPermanent.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val searchType =
        future
            .map {
                when (it.onImportMatcher) {
                    is TransactionMatcher.SearchText ->
                        SearchType.DESCRIPTION
                    is TransactionMatcher.ByValue ->
                        SearchType.TOTAL
                    is TransactionMatcher.Multi ->
                        if (it.onImportMatcher.transactionMatchers.all { it is TransactionMatcher.SearchText })
                            SearchType.DESCRIPTION
                        else
                            SearchType.DESCRIPTION_AND_TOTAL
                }
            }
            .flatMapLatest { userSetSearchType.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SearchType.DESCRIPTION)
    private val categoryAmountFormulas =
        future
            .map { it.categoryAmountFormulas }
            .flatMapLatest { oldCategoryAmountFormulas ->
                combine(userCategoryAmountFormulas.flow, selectedCategoriesModel.selectedCategories)
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
                selectedCategoriesModel.selectedCategories.drop(1)
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
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value.ZERO)

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToCategorySelection = MutableSharedFlow<Unit>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val otherInput =
        searchType.map { searchType ->
            listOfNotNull(
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Name"),
                    EditTextVMItem(text = name.value!!, onDone = { userSetName(it) }),
                ),
                listOf(
                    TextPresentationModel(
                        style = TextPresentationModel.Style.TWO,
                        text2 = this.searchType
                            .map {
                                when (it) {
                                    SearchType.DESCRIPTION -> "Total Guess"
                                    SearchType.TOTAL,
                                    SearchType.DESCRIPTION_AND_TOTAL,
                                    -> "Exact Total"
                                }
                            }
                    ),
                    MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = { userSetTotalGuess(it) }),
                ),
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                    SpinnerVMItem(SearchType.values(), searchType, onNewItem = { userSetSearchType(it) }),
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
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Is Permanent"),
                    CheckboxVMItem(isPermanent.value, onCheckChanged = { userSetIsPermanent(it) }),
                ),
            )
        }
    val recipeGrid =
        combine(categoryAmountFormulas.flatMapSourceHashMap { it.itemFlowMap }, fillCategory)
        { categoryAmountFormulaItemFlows, fillCategory ->
            categoryAmountFormulaItemFlows.map { (category, amountFormula) ->
                CategoryAmountFormulaPresentationModel(category, fillCategory, if (category == fillCategory) fillAmountFormula else amountFormula, { userSetFillCategory(it.name) }, { userSetCategoryAmountFormula(category, it) }).toHasToViewItemRecipes()
            }
        }
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                    ),
                    *it.toTypedArray(),
                )
            }
    val dividerMap =
        categoryAmountFormulas
            .map {
                it.map { it.key }.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 1 } // header row
                    .mapValues { DividerVMItem(it.value) }
            }
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Delete",
                    onClick = { userDeleteFutureOrReplay() },
                ),
                ButtonVMItem(
                    title = "Add Or Remove Categories",
                    onClick = { userTryNavToCategorySelection() },
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = { userTrySubmit() },
                ),
            )
        )
}