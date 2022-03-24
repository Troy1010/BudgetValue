package com.tminus1010.budgetvalue._unrestructured.reconcile.domain

class ReconciliationAggregate(private val reconciliations: List<Reconciliation>) {
    val mostRecent get() = reconciliations.sortedBy { it.localDate }.lastOrNull()
}