package com.tminus1010.budgetvalue.ui.create_future

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.NoDescriptionEnteredException
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.framework.view.Toaster
import com.tminus1010.budgetvalue.ui.all_features.model.*
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.TotalFuture
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
class CreateFuture2VM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectedCategoriesModel: SelectCategoriesModel,
    private val futuresRepo: FuturesRepo,
    private val toaster: Toaster,
    private val categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        saveReplayDialogBox.onNext(
            categoryAmountFormulas.value
                .map { (category, amountFormula) ->
                    if (category != fillCategory.value)
                        amountFormula.toDisplayStr2() + " " + category.name
                    else
                        category.name
                }
                .joinToString(", ")
        )
    }

    fun userTrySubmitWithName(s: String) {
        try {
            val futureToPush =
                when (searchType.value) {
                    SearchType.DESCRIPTION_AND_TOTAL ->
                        TODO()
                    SearchType.TOTAL ->
                        TotalFuture(
                            name = s,
                            searchTotal = totalGuess.value,
                            categoryAmountFormulas = categoryAmountFormulas.value,
                            fillCategory = fillCategory.value!!,
                            terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.WAITING_FOR_MATCH,
                            isAutomatic = isAutomatic.value,
                        )
                    SearchType.DESCRIPTION ->
                        BasicFuture(
                            name = s,
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
                if (futureToPush.terminationStrategy == TerminationStrategy.PERMANENT) {
                    val number = categorizeAllMatchingUncategorizedTransactions(futureToPush).blockingGet()
                    toaster.toast("$number transactions categorized")
                }
                selectedCategoriesModel.clearSelection()
                navUp.emit(Unit)
            }
        } catch (e: Throwable) {
            when (e) {
                is NoDescriptionEnteredException -> toaster.toast("Fill description or use another search type")
                else -> throw e
            }
        }
    }

    private val totalGuess = MutableStateFlow(BigDecimal("-10"))
    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    private val isPermanent = MutableStateFlow(false)
    fun userSetIsPermanent(b: Boolean) {
        isPermanent.onNext(b)
    }

    private val isAutomatic = MutableStateFlow(true)
    fun userSetIsAutomatic(b: Boolean) {
        isAutomatic.onNext(b)
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

    // # Internal
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
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()
    val saveReplayDialogBox = MutableSharedFlow<String>()

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