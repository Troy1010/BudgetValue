package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import io.reactivex.rxjava3.core.Observable

class CategoryAmountFormulaVMItem(
    val category: Category,
    amountFormula: Observable<AmountFormula>,
    fillCategoryAmount: Observable<Pair<Category, AmountFormula>>,
) {
    /**
     * should emit when amountFormula emits, or when the fillCategory changes from or to this.category.
     */
    val amountFormula: Observable<AmountFormula> =
        Observable.combineLatest(
            amountFormula,
            fillCategoryAmount
                .pairwise()
                .filter { listOf(it.first.first, it.second.first).any { it == category } }
                .map { it.second }
                .startWith(fillCategoryAmount.take(1)),
            ::getAmountFormula
        )

    private fun getAmountFormula(amountFormula: AmountFormula, fillCategoryAmount: Pair<Category, AmountFormula>): AmountFormula {
        return if (fillCategoryAmount.first == category)
            fillCategoryAmount.second
        else
            amountFormula
    }
}
