package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.asObservable2
import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._core.all.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue._core.framework.ColdObservable
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.retryWithDelay
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * Base class for common code when creating ViewModels which allow the user to input a CategoryAmountFormula
 */
abstract class CategoryAmountFormulaVMItemsBaseVM : ViewModel() {
    abstract val categoriesInteractor: CategoriesInteractor
    abstract val selectCategoriesModel: SelectCategoriesModel

    // # User Intents
    protected val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.isZero)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    protected val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    fun userSetCategoryIsPercentage(category: Category, isPercentage: Boolean) {
        userCategoryIsPercentage[category] = isPercentage
    }

    protected val userSetFillCategory = BehaviorSubject.create<Box<Category?>>()
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(Box(categoriesInteractor.parseCategory(categoryName)))
    }

    // # Internal
    protected open val _selectedCategories =
        Observable.defer { selectCategoriesModel.selectedCategories.asObservable2() }
            .retryWithDelay(200, TimeUnit.MILLISECONDS)
    protected val selectedCategories =
        Observable.defer { _selectedCategories }.cold()

    // # Output
    val totalGuessHeader = "Total Guess"
    protected abstract val _totalGuess: ColdObservable<BigDecimal>
    val totalGuess = Observable.defer { _totalGuess }.cold()

    open val _fillCategory =
        selectedCategories
            .map { selectedCategories ->
                (selectedCategories.find { it.defaultAmountFormula.isZero() }
                    ?: selectedCategories.getOrNull(0))
                    .let { Box(it) }
            }
            .switchMap { userSetFillCategory.startWithItem(it) }
            .distinctUntilChanged()
            .replayNonError(1)
            .cold()
    val fillCategory = Observable.defer { _fillCategory }.cold()

    protected val userCategoryAmountFormulas =
        Observable.combineLatest(userCategoryAmounts.observable, userCategoryIsPercentage.observable, selectedCategories)
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
            .replayNonError(1)

    @VisibleForTesting
    open val _categoryAmountFormulas =
        Observable.combineLatest(userCategoryAmountFormulas, selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas)
        }
            .replayNonError(1)
            .cold()
    val categoryAmountFormulas = Observable.defer { _categoryAmountFormulas }.cold()

    @VisibleForTesting
    val fillAmountFormula =
        Observable.combineLatest(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, (fillCategory), total ->
            fillCategory
                ?.let { categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory] }
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .cold()

    val categoryHeader = "Category"
    val amountHeader = "Amount"
    val fillHeader = "Fill"
    val categoryAmountFormulaVMItems: Observable<List<CategoryAmountFormulaVMItem>> =
        categoryAmountFormulas
            .flatMapSourceHashMap { it.itemObservableMap }
            .map { categoryAmountFormulaItemObservables ->
                categoryAmountFormulaItemObservables.map { (category, amountFormula) ->
                    CategoryAmountFormulaVMItem(category, amountFormula, fillCategory, fillAmountFormula, ::userSetCategoryIsPercentage, ::userInputCA)
                }
            }
            .replayNonError(1)
}