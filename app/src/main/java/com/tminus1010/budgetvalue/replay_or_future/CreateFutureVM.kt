package com.tminus1010.budgetvalue.replay_or_future

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.SingleSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFutureVM @Inject constructor(

) : ViewModel() {
    // # Workarounds
    fun setup(categorySelectionVM: CategorySelectionVM) {
        categorySelectionVM.selectedCategories.observe(disposables) { selectedCategories.onSuccess(it) }
    }

    // # Input
    private val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.isZero)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    private val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    fun userSwitchCategoryIsPercentage(category: Category, isPercentage: Boolean) {
        userCategoryIsPercentage[category] = isPercentage
    }

    private val userSetFillCategory = BehaviorSubject.create<Category>()!!
    fun userSetFillCategory(category: Category) {
        userSetFillCategory.onNext(category)
    }

    fun userSubmit() {
        TODO()
    }

    // # Internal
    private val selectedCategories = SingleSubject.create<List<Category>>()
    private val total: Observable<BigDecimal> = Observable.just(BigDecimal.ZERO)
    private val userCategoryAmountFormulas =
        Rx.combineLatest(
            userCategoryAmounts.observable,
            userCategoryIsPercentage.observable,
        )
            .map { (userCategoryAmounts, userCategoryIsPercentage) ->
                (userCategoryAmounts.keys + userCategoryIsPercentage.keys)
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
        Rx.combineLatest(
            userCategoryAmountFormulas,
            selectedCategories.toObservable(),
        )
            .map { (userCategoryAmountFormulas, selectedCategories) ->
                CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                    .plus(userCategoryAmountFormulas)
            }
            .nonLazyCache(disposables)

    // # Output
    val buttonVMItems =
        listOf(
            ButtonVMItem(
                "Submit",
                onClick = ::userSubmit
            )
        )
    val navUp = PublishSubject.create<Unit>()

    val fillCategory =
        userSetFillCategory
            .startWith(
                selectedCategories
                    .map { selectedCategories ->
                        selectedCategories.find { it.defaultAmountFormula.isZero() }
                            ?: selectedCategories.getOrNull(0)
                            ?: CategoriesDomain.defaultCategory
                    }
            )
            .distinctUntilChanged()
            .nonLazyCache(disposables)

    @VisibleForTesting
    val fillCategoryAmountFormula =
        Rx.combineLatest(
            categoryAmountFormulas,
            fillCategory,
            total,
        )
            .map { (categoryAmountFormulas, fillCategory, total) ->
                Pair(fillCategory, categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]!!)
            }!!

    val categoryHeader = "Category"
    val amountHeader = "Amount"
    val fillHeader = "Fill"
    val categoryAmountFormulaVMItems: Observable<List<CategoryAmountFormulaVMItem>> =
        categoryAmountFormulas.flatMapSourceHashMap { it.itemObservableMap }
            .map { categoryAmountFormulaItemObservables ->
                categoryAmountFormulaItemObservables.map { (category, amountFormula) ->
                    CategoryAmountFormulaVMItem(category, amountFormula, fillCategoryAmountFormula)
                }
            }
            .nonLazyCache(disposables)
}