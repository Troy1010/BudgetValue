package com.example.budgetvalue.model_app

import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class ReconcileRowData (
    val category: Category,
    val actual: BigDecimal,
    val reconcile: BehaviorSubject<BigDecimal>
) {
    val budgeted = actual + reconcile.value
    fun getBudgeted2(incomeValue: BigDecimal): BigDecimal { // TODO: hacky
        return actual + incomeValue
    }
}