package com.example.budgetvalue.model_app

import com.example.budgetvalue.combineLatestAsTuple
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class ReconcileRowData (
    val category: Category,
    val actual: BehaviorSubject<BigDecimal>,
    val reconcile: BehaviorSubject<BigDecimal>
) {
    val budgeted = combineLatestAsTuple(actual, reconcile)
        .map { it.first + it.second }
    // TODO("This should also add from previous blocks")
}