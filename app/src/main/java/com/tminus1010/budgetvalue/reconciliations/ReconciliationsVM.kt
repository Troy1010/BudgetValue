package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReconciliationsVM @Inject constructor(
    private val reconciliationsRepo: IReconciliationsRepo
) : ViewModel() {
    val reconciliations = reconciliationsRepo.reconciliations
    // # Intents
    fun delete(reconciliation: Reconciliation) {
        Rx.launch { reconciliationsRepo.delete(reconciliation) }
    }
}