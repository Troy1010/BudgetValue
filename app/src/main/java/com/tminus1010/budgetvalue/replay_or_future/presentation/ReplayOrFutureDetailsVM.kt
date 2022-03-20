package com.tminus1010.budgetvalue.replay_or_future.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.all.extensions.onNext
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.framework.view.Toaster
import com.tminus1010.budgetvalue._core.presentation.model.*
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.*
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.transactions.presentation.model.SearchType
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
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
        TODO()
    }

    fun userSetTotalGuess(s: String) {
        _totalGuess.onNext(s.toMoneyBigDecimal())
    }

    fun userSetIsPermanent(b: Boolean) {
        _isPermanent.onNext(b)
    }

    fun userSetIsAutomatic(b: Boolean) {
        _isAutomatic.onNext(b)
    }

    fun userSetSearchType(searchType: SearchType) {
        this._searchType.onNext(searchType)
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

    fun userDeleteFutureOrReplay() {
        when (val x = replayOrFuture.replayCache[0]) {
            is BasicFuture -> futuresRepo.delete(x).subscribe()
            is BasicReplay,
            is TotalFuture -> TODO()
            else -> error("Oh no!")
        }
        navUp.onNext()
    }

    // # Internal
    private val _totalGuess = MutableSharedFlow<BigDecimal>()
    private val totalGuess =
        merge(
            _totalGuess,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> it.totalGuess
                        else -> error("Unhandled type:$it")
                    }
                },
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal("-10"))
    private val _isPermanent = MutableSharedFlow<Boolean>()
    private val isPermanent =
        merge(
            _isPermanent,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> it.terminationStrategy == TerminationStrategy.PERMANENT
                        else -> error("Unhandled type:$it")
                    }
                },
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val _isAutomatic = MutableSharedFlow<Boolean>()
    private val isAutomatic =
        merge(
            _isAutomatic,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> it.isAutomatic
                        else -> error("Unhandled type:$it")
                    }
                },
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    private val _searchType = MutableSharedFlow<SearchType>()
    private val searchType =
        merge(
            _searchType,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> SearchType.DESCRIPTION
                        else -> error("Unhandled type:$it")
                    }
                },
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, SearchType.DESCRIPTION)
    private val _categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesModel.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
    private val categoryAmountFormulas =
        merge(
            _categoryAmountFormulas,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> it.categoryAmountFormulas
                        else -> error("Unhandled type:$it")
                    }
                },
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())
    private val _fillCategory =
        selectedCategoriesModel.selectedCategories
            .flatMapLatest { selectedCategories ->
                userSetFillCategory
                    .onStart { emit(selectedCategories.find { it.defaultAmountFormula.isZero() } ?: selectedCategories.getOrNull(0)) }
            }
    private val fillCategory =
        merge(
            _fillCategory,
            replayOrFuture
                .map {
                    when (it) {
                        is BasicFuture -> it.fillCategory
                        else -> error("Unhandled type:$it")
                    }
                },
        )
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