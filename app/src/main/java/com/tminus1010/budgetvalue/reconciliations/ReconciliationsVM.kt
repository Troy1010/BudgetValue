package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import io.reactivex.rxjava3.subjects.PublishSubject

class ReconciliationsVM(
    domainFacade: DomainFacade
) : ViewModel() {
    val reconciliations = domainFacade.reconciliations
    val intentDeleteReconciliation = PublishSubject.create<Reconciliation>()
        .apply { launch { domainFacade.delete(it) } }
}