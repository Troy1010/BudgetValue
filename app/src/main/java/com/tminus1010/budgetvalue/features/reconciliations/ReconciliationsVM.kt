package com.tminus1010.budgetvalue.features.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.features_shared.Domain
import io.reactivex.rxjava3.subjects.PublishSubject

class ReconciliationsVM(
    domain: Domain
) : ViewModel() {
    val reconciliations = domain.reconciliations
    val intentDeleteReconciliation = PublishSubject.create<Reconciliation>()
        .apply { launch { domain.delete(it) } }
}