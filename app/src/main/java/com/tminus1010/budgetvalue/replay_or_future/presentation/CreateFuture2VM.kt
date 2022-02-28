package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.*
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.presentation.model.*
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.presentation.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFuture2VM @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
    private val selectedCategoriesModel: SelectCategoriesModel,
) : ViewModel() {
    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    fun userSubmit() {
        TODO()
        navUp.onNext()
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

    fun userSetDescription(s: String) {
        description.onNext(s)
    }

    private val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.isZero)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    private val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    fun userSetCategoryIsPercentage(category: Category, isPercentage: Boolean) {
        userCategoryIsPercentage[category] = isPercentage
    }

    private val userSetFillCategory = MutableStateFlow<Category?>(null)
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoriesInteractor.parseCategory(categoryName))
    }

    // # Internal
    private val totalGuess = MutableStateFlow(BigDecimal.TEN)
    private val isPermanent = MutableStateFlow(false)
    private val searchType = MutableStateFlow(SearchType.DESCRIPTION)
    private val description = MutableStateFlow<String?>(null)

    // TODO: Do I need all of this? Is there an easier way..?

    private val userCategoryAmountFormulas =
        combine(userCategoryAmounts.flow, userCategoryIsPercentage.flow, selectedCategoriesModel.selectedCategories)
        { userCategoryAmounts, userCategoryIsPercentage, selectedCategories ->
            (userCategoryAmounts.keys + userCategoryIsPercentage.keys)
                .filter { it in selectedCategories }
                .associateWith {
                    if (userCategoryIsPercentage[it] ?: false)
                        AmountFormula.Percentage(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                    else
                        AmountFormula.Value(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                }
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, mapOf()) // TODO: Is this necessary?
    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas, selectedCategoriesModel.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas()) // TODO: Is this necessary?
    private val fillCategory =
        selectedCategoriesModel.selectedCategories
            .flatMapLatest { selectedCategories ->
                userSetFillCategory.onStart {
                    emit(
                        selectedCategories.find { it.defaultAmountFormula.isZero() }
                            ?: selectedCategories.getOrNull(0)
                    )
                }
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null) // TODO: Is this necessary?

    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            fillCategory
                ?.let { categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory] }
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .let { runBlocking { it.stateIn(viewModelScope) } }


    private val categoryAmountFormulaPartialRecipeGrid =
        categoryAmountFormulas
            .flatMapSourceHashMap { it.itemFlowMap }
            .map { categoryAmountFormulaItemFlows ->
                categoryAmountFormulaItemFlows.map { (category, amountFormula) ->
//                    CategoryAmountFormulaVMItem(category, amountFormula, fillCategory, fillAmountFormula, ::userSetCategoryIsPercentage, ::userInputCA)
                    CategoryAmountFormulaPresentationModel(category).toHasToViewItemRecipes()
                }
            }


    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToCategorySelection = MutableSharedFlow<Unit>()

    // # State
    val otherInput =
        searchType.map { searchType ->
            listOfNotNull(
                listOf(
                    TextPresentationModel(text1 = "Total Guess"),
                    MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = { userSetTotalGuess(it) }),
                ),
                listOf(
                    TextPresentationModel(text1 = "Search Type"),
                    SpinnerVMItem(SearchType.values(), searchType, onNewItem = { userSetSearchType(it) }),
                ),
                if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
                    listOf(
                        TextPresentationModel(text1 = "Description"),
                        EditTextVMItem(text = description.value, onDone = { userSetDescription(it) }),
                    )
                else null,
                listOf(
                    TextPresentationModel(text1 = "Is Permanent"),
                    CheckboxVMItem(isPermanent.value, onCheckChanged = { userSetIsPermanent(it) }),
                ),
            )
        }
    val recipeGrid =
        categoryAmountFormulaPartialRecipeGrid
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                    ),
                    *it.toTypedArray()
                )
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
                    onClick = { userSubmit() },
                ),
            )
        )
}