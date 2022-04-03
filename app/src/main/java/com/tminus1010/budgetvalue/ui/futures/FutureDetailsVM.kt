package com.tminus1010.budgetvalue.ui.futures

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.app.CategorizeTransactions
import com.tminus1010.budgetvalue.app.CategoryAdapter
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.domain.*
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.budgetvalue.ui.all_features.model.SearchType
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class FutureDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val categoryAdapter: CategoryAdapter,
    private val selectedCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val futuresRepo: FuturesRepo,
    private val showToast: ShowToast,
    private val categorizeTransactions: CategorizeTransactions,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
) : ViewModel() {
    // # User Intents
    fun userTryNavToChooseCategories() {
        navToChooseCategories.easyEmit()
    }

    fun userTryNavToSetSearchTexts() {
        navToSetSearchTexts.onNext()
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesSharedVM.clearSelection() }
        navUp.onNext()
    }

    fun userDeleteFuture() {
        runBlocking { futuresRepo.delete(originalFuture) }
        userTryNavUp()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        try {
            if (futureToPush.value.name == "") throw InvalidNameException()
            if (futureToPush.value.fillCategory == Category.UNRECOGNIZED) throw InvalidFillCategoryException()
            runBlocking {
                futuresRepo.push(futureToPush.value)
                if (futureToPush.value.terminationStrategy == TerminationStrategy.PERMANENT)
                    categorizeTransactions({ futureToPush.value.onImportTransactionMatcher?.isMatch(it) ?: false }, futureToPush.value::categorize)
                        .also { showToast(NativeText.Simple("$it transactions categorized")) }
                if (futureToPush.value.name != originalFuture.name) futuresRepo.delete(originalFuture)
                userTryNavUp()
            }
        } catch (e: Throwable) {
            when (e) {
                is InvalidFillCategoryException -> showToast(NativeText.Simple("Invalid fill category"))
                is InvalidNameException -> showToast(NativeText.Simple("Invalid name"))
                else -> throw e
            }
        }
    }

    fun userSetTotalGuess(s: String) {
        futureToPush.onNext(futureToPush.value.copy(totalGuess = s.toMoneyBigDecimal()))
    }

    fun userSetIsOnlyOnce(b: Boolean) {
        futureToPush.onNext(futureToPush.value.copy(terminationStrategy = if (b) TerminationStrategy.ONCE else TerminationStrategy.PERMANENT))
    }

    fun userSetSearchType(searchType: SearchType) {
        futureToPush.onNext(futureToPush.value.copy(
            onImportTransactionMatcher = when (searchType) {
                SearchType.DESCRIPTION -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) })
                SearchType.DESCRIPTION_AND_TOTAL -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) }.plus(TransactionMatcher.ByValue(futureToPush.value.totalGuess)))
                SearchType.TOTAL -> TransactionMatcher.ByValue(futureToPush.value.totalGuess)
                SearchType.NONE -> null
            }
        ))
    }

    fun userSetName(s: String) {
        futureToPush.onNext(futureToPush.value.copy(name = s))
    }

    private val userCategoryAmountFormulas = SourceHashMap<Category, AmountFormula>()
    fun userSetCategoryAmountFormula(category: Category, amountFormula: AmountFormula) {
        if (amountFormula.isZero())
            userCategoryAmountFormulas.remove(category)
        else
            userCategoryAmountFormulas[category] = amountFormula
    }

    private val userSetFillCategory = MutableSharedFlow<Category>()
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoryAdapter.parseCategory(categoryName))
    }

    // # Internal
    private val originalFuture = moshiWithCategoriesProvider.moshi.fromJson<Future>(savedStateHandle.get<String>(KEY1))!!
    private val futureToPush = MutableStateFlow(originalFuture)
    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesSharedVM.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(originalFuture.categoryAmountFormulas)
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())

    init {
        categoryAmountFormulas.observe(viewModelScope) { futureToPush.onNext(futureToPush.value.copy(categoryAmountFormulas = it)) }
    }

    private val fillCategory =
        selectedCategoriesSharedVM.selectedCategories
            .flatMapLatest { userSetFillCategory.onStart { emit(it.find { it.defaultAmountFormula.isZero() } ?: it.getOrNull(0) ?: Category.UNRECOGNIZED) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Category.UNRECOGNIZED)

    init {
        fillCategory.observe(viewModelScope) { futureToPush.onNext(futureToPush.value.copy(fillCategory = it)) }
    }

    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, futureToPush.map { it.totalGuess })
        { categoryAmountFormulas, fillCategory, total ->
            categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value(BigDecimal.ZERO))

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToChooseCategories = MutableSharedFlow<Unit>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val otherInputTableView =
        futureToPush.map { transactionMatcherPresentationFactory.searchType(it.onImportTransactionMatcher) }.distinctUntilChanged().map { searchType ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Name"),
                        EditTextVMItem2(textFlow = futureToPush.map { it.name }, onDone = ::userSetName),
                    ),
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(SearchType.values(), searchType, onNewItem = ::userSetSearchType),
                    ),
                    listOf(
                        TextPresentationModel(
                            style = TextPresentationModel.Style.TWO,
                            text1 = when (searchType) {
                                SearchType.NONE,
                                SearchType.DESCRIPTION,
                                -> "Total Guess"
                                SearchType.TOTAL,
                                SearchType.DESCRIPTION_AND_TOTAL,
                                -> "Exact Total"
                            }
                        ),
                        MoneyEditVMItem(text2 = futureToPush.map { it.totalGuess.toString() }.distinctUntilChanged(), onDone = ::userSetTotalGuess),
                    ),
                    if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
                        listOf(
                            TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Texts"),
                            ButtonVMItem(
                                title = "View Search Texts",
                                onClick = ::userTryNavToSetSearchTexts,
                            ),
                        )
                    else null,
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Is Only Once"),
                        CheckboxVMItem(futureToPush.value.terminationStrategy == TerminationStrategy.ONCE, onCheckChanged = ::userSetIsOnlyOnce),
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
                    onClick = ::userDeleteFuture,
                ),
                ButtonVMItem(
                    title = "Add Or Remove Categories",
                    onClick = ::userTryNavToChooseCategories,
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userTrySubmit,
                ),
            )
        )
}