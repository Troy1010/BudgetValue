package com.tminus1010.budgetvalue.all.domain.models

import com.tminus1010.budgetvalue.reconcile.app.Reconciliation

class Reconciliations(private val reconciliations: List<Reconciliation>) {
    val mostRecent get() = reconciliations.sortedBy { it.localDate }.lastOrNull()
}