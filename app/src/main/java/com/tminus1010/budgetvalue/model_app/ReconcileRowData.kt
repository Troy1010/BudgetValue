package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.sum
import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class ReconcileRowData(
    val category: Category,
    val plan: Observable<BigDecimal>,
    val actual: Observable<BigDecimal>,
    val reconcile: Observable<BigDecimal>
) {
    val budgeted = combineLatestAsTuple(plan, actual, reconcile)
        .map { it.toList().sum() }
    // TODO("This should also add from previous blocks")
}