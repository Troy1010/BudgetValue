package com.example.budgetvalue.model_app

import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.sum
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class ReconcileRowData (
    val category: Category,
    val plan: Observable<BigDecimal>,
    val actual: Observable<BigDecimal>,
    val reconcile: BehaviorSubject<BigDecimal>
) {
    val budgeted = combineLatestAsTuple(plan, actual, reconcile)
        .map { it.toList().sum() }
    // TODO("This should also add from previous blocks")
}