package com.tminus1010.budgetvalue.replay_or_future

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.ColdObservable
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.retryWithDelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * Base class for common code when creating ViewModels which allow the user to input a CategoryAmountFormula
 */
abstract class CategoryAmountFormulaVMItemsBaseVM : ViewModel() {
    abstract val categoryParser: ICategoryParser

    // # Workarounds
    /**
     * Expected to be setup
     */
    lateinit var categorySelectionVM: CategorySelectionVM

    // # Input
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

    private val userSetFillCategory = BehaviorSubject.create<Category>()!!
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoryParser.parseCategory(categoryName))
    }

    // # Internal
    private val selectedCategories =
        Observable.defer { categorySelectionVM.selectedCategories }
            .retryWithDelay(200, TimeUnit.MILLISECONDS, 99999)

    // # Output
    val totalGuessHeader = "Total Guess"
    abstract val _totalGuess: ColdObservable<BigDecimal>
    val totalGuess = Observable.defer { _totalGuess }.cold()

    val fillCategory =
        selectedCategories
            .map { selectedCategories ->
                selectedCategories.find { it.defaultAmountFormula.isZero() }
                    ?: selectedCategories.getOrNull(0)
                    ?: CategoriesDomain.defaultCategory
            }
            .switchMap { userSetFillCategory.startWithItem(it) }
            .distinctUntilChanged()
            .nonLazyCache(disposables)
            .cold()

    private val userCategoryAmountFormulas =
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
            .nonLazyCache(disposables)

    @VisibleForTesting
    val categoryAmountFormulas =
        Observable.combineLatest(userCategoryAmountFormulas, selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas)
        }
            .nonLazyCache(disposables)
            .cold()

    @VisibleForTesting
    val fillAmountFormula =
        Observable.combineLatest(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]
                ?: AmountFormula.Value(BigDecimal.ZERO) // Occurs when fillCategory is defaultCategory
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
            .nonLazyCache(disposables)
}