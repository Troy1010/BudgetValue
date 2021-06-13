package com.tminus1010.budgetvalue.reconciliations

import com.tminus1010.budgetvalue._core.BaseViewModel
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReconciliationsVM @Inject constructor(
    private val reconciliationsRepo: IReconciliationsRepo
) : BaseViewModel() {
    // # Intents
    fun delete(reconciliation: Reconciliation) {
        reconciliationsRepo.delete(reconciliation).observe(disposables)
    }
}