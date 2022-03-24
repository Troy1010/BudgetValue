package com.tminus1010.budgetvalue.ui.create_future

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.NoDescriptionEnteredException
import com.tminus1010.budgetvalue.all_layers.extensions.*
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.framework.view.Toaster
import com.tminus1010.budgetvalue.ui.all_features.model.*
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.*
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.budgetvalue.domain.AmountFormula
import com.tminus1010.budgetvalue._unrestructured.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model.SearchType
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
    private val categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
    // # Setup
    val replayOrFuture = MutableSharedFlow<IReplayOrFuture>(1)

    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        try {
            val futureToPush =
                when (searchType.value) {
                    SearchType.DESCRIPTION_AND_TOTAL ->
                        TODO()
                    SearchType.TOTAL ->
                        TotalFuture(
                            name = name.value ?: throw NoDescriptionEnteredException(),
                            searchTotal = totalGuess.value,
                            categoryAmountFormulas = categoryAmountFormulas.value,
                            fillCategory = fillCategory.value!!,
                            terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.WAITING_FOR_MATCH,
                            isAutomatic = isAutomatic.value,
                        )
                    SearchType.DESCRIPTION ->
                        BasicFuture(
                            name = name.value ?: throw NoDescriptionEnteredException(),
                            searchTexts = setSearchTextsSharedVM.searchTexts.value,
                            categoryAmountFormulas = categoryAmountFormulas.value,
                            fillCategory = fillCategory.value!!,
                            terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.WAITING_FOR_MATCH,
                            isAutomatic = isAutomatic.value,
                            totalGuess = totalGuess.value,
                        )
                }
            runBlocking {
                futuresRepo.push(futureToPush)
                if (futureToPush.name != replayOrFuture.value!!.name) futuresRepo.delete(replayOrFuture.value!! as IFuture)
                if (futureToPush.terminationStrategy == TerminationStrategy.PERMANENT) {
                    val number = categorizeAllMatchingUncategorizedTransactions(futureToPush).blockingGet()
                    toaster.toast("$number transactions categorized")
                }
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

    private val userSetIsAutomatic = MutableSharedFlow<Boolean>()
    fun userSetIsAutomatic(b: Boolean) {
        userSetIsAutomatic.onNext(b)
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
        when (val x = replayOrFuture.replayCache[0]) {
            is BasicFuture -> runBlocking { futuresRepo.delete(x) }
            is BasicReplay,
            is TotalFuture,
            -> TODO()
            else -> error("Oh no!")
        }
        userTryNavUp()
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesModel.clearSelection() }
        navUp.onNext()
    }

    // # Internal
    private val name =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.name
                    else -> error("Unhandled type:$it")
                }
            }
            .flatMapLatest { userSetName.onStart { emit(it) } }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    private val totalGuess =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.totalGuess
                    else -> error("Unhandled type:$it")
                }
            }
            .flatMapLatest { userSetTotalGuess.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal("-10"))
    private val isPermanent =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.terminationStrategy == TerminationStrategy.PERMANENT
                    else -> error("Unhandled type:$it")
                }
            }
            .flatMapLatest { userSetIsPermanent.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val isAutomatic =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.isAutomatic
                    else -> error("Unhandled type:$it")
                }
            }
            .flatMapLatest { userSetIsAutomatic.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    private val searchType =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> SearchType.DESCRIPTION
                    else -> error("Unhandled type:$it")
                }
            }
            .flatMapLatest { userSetSearchType.onStart { emit(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SearchType.DESCRIPTION)
    private val categoryAmountFormulas =
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.categoryAmountFormulas
                    else -> error("Unhandled type:$it")
                }
            }
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
        replayOrFuture
            .map {
                when (it) {
                    is BasicFuture -> it.fillCategory
                    else -> error("Unhandled type:$it")
                }
            }
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
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Total Guess"),
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
                            title = "View Search Texts (${setSearchTextsSharedVM.searchTexts.value.size})",
                            onClick = { userTryNavToSetSearchTexts() },
                        ),
                    )
                else null,
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Is Permanent"),
                    CheckboxVMItem(isPermanent.value, onCheckChanged = { userSetIsPermanent(it) }),
                ),
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Is Automatic"),
                    CheckboxVMItem(isAutomatic.value, onCheckChanged = { userSetIsAutomatic(it) }),
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