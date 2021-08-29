package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import io.reactivex.rxjava3.core.Observable

data class CategoryAmountFormulaVMItem(
    val category: Category,
    private val _amountFormula: Observable<AmountFormula>,
    private val fillCategoryAmountFormula: Observable<Pair<Category, AmountFormula>>,
) {
    /**
     * should emit when amountFormula emits, or when the fillCategory changes from or to this.category.
     */
    val amountFormula: Observable<AmountFormula> =
        Observable.combineLatest(
            _amountFormula,
            fillCategoryAmountFormula
                .pairwise()
                .filter { listOf(it.first.first, it.second.first).any { it == category } }
                .map { it.second }
                .startWith(fillCategoryAmountFormula.take(1)),
            ::getAmountFormula
        )

    /**
     * If category == fillCategory, use the fillCategory's amountFormula instead.
     */
    private fun getAmountFormula(amountFormula: AmountFormula, fillCategoryAmount: Pair<Category, AmountFormula>): AmountFormula {
        return if (fillCategoryAmount.first == category)
            fillCategoryAmount.second
        else
            amountFormula
    }
}
