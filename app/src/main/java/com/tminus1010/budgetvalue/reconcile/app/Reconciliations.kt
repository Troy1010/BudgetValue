package com.tminus1010.budgetvalue.reconcile.app

class Reconciliations(private val reconciliations: List<Reconciliation>) {
    val mostRecent get() = reconciliations.sortedBy { it.localDate }.lastOrNull()
}