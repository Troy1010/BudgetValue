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
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
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
class CreateFuture2VM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectedCategoriesModel: SelectCategoriesModel,
    private val futuresRepo: FuturesRepo,
    private val toaster: Toaster,
    private val categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        try {
            when (searchType.value) {
                SearchType.DESCRIPTION_AND_TOTAL ->
                    TODO()
                SearchType.TOTAL ->
                    TotalFuture(
                        name = generateUniqueID(),
                        searchTotal = totalGuess.value,
                        categoryAmountFormulas = categoryAmountFormulas.value,
                        fillCategory = fillCategory.value!!,
                        terminationStatus = if (isPermanent.value) TerminationStatus.PERMANENT else TerminationStatus.WAITING_FOR_MATCH,
                    )
                SearchType.DESCRIPTION ->
                    BasicFuture(
                        name = generateUniqueID(),
                        searchText = description.value?.ifEmpty { null } ?: throw NoDescriptionEnteredException(),
                        categoryAmountFormulas = categoryAmountFormulas.value,
                        fillCategory = fillCategory.value!!,
                        terminationStatus = if (isPermanent.value) TerminationStatus.PERMANENT else TerminationStatus.WAITING_FOR_MATCH,
                    )
            }
                .let { newFuture ->
                    Rx.merge(
                        futuresRepo.add(newFuture),
                        if (newFuture.terminationStatus == TerminationStatus.PERMANENT) categorizeAllMatchingUncategorizedTransactions(newFuture).doOnSuccess { toaster.toast("$it transactions categorized") }.ignoreElement() else null,
                    )
                }
                .andThen(Completable.fromAction { runBlocking { selectedCategoriesModel.clearSelection() } }.subscribeOn(Schedulers.io()))
                .andThen(Completable.fromAction { navUp.onNext(Unit) })
                .subscribe()
        } catch (e: Throwable) {
            when (e) {
                is NoDescriptionEnteredException -> toaster.toast("Fill description or use another search type")
                else -> throw e
            }
        }
    }

    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    fun userSetIsPermanent(b: Boolean) {
        isPermanent.onNext(b)
    }

    fun userSetSearchType(searchType: SearchType) {
        this.searchType.onNext(searchType)
    }

    private val userSetDescription = MutableSharedFlow<String?>()
    fun userSetDescription(s: String) {
        userSetDescription.onNext(s)
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

    // # Internal
    private val totalGuess = MutableStateFlow(BigDecimal.TEN)
    private val isPermanent = MutableStateFlow(false)
    private val searchType = MutableStateFlow(SearchType.DESCRIPTION)
    private val description =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .flatMapLatest { userSetDescription.onStart { emit(it?.description) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesModel.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())
    private val fillCategory =
        selectedCategoriesModel.selectedCategories
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
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Description"),
                        EditTextVMItem(text = description.value, onDone = { userSetDescription(it) }),
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