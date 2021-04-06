package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue.reconciliations.domain.ReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class ReconciliationsVM @Inject constructor(
    reconciliationDomain: ReconciliationDomain
) : ViewModel() {
    val reconciliations = reconciliationDomain.reconciliations
    val intentDeleteReconciliation = PublishSubject.create<Reconciliation>()
        .apply { launch { reconciliationDomain.delete(it) } }
}