package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items.UnformattedString
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.reconcile.app.convenience_service.ReconciliationsToDo
import com.tminus1010.budgetvalue.reconcile.app.interactor.SaveActiveReconciliationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    reconciliationsToDo: ReconciliationsToDo,
    saveActiveReconciliationInteractor: SaveActiveReconciliationInteractor,
) : ViewModel() {
    // # Presentation State
    val currentReconciliationToDo =
        reconciliationsToDo.mapBox { it.firstOrNull() }
    val title =
        currentReconciliationToDo.map { (it) ->
            when (it) {
                is ReconciliationToDo.Accounts -> "Accounts Reconciliation"
                is ReconciliationToDo.PlanZ -> "Plan Reconciliation"
                null,
                is ReconciliationToDo.Anytime -> "Anytime Reconciliation"
            }.let { UnformattedString(it) }
        }
    val subTitle =
        reconciliationsToDo.map {
            when (it.size) {
                0 -> UnformattedString(R.string.reconciliations_required_none)
                1 -> UnformattedString(R.string.reconciliations_required_one)
                else -> UnformattedString(R.string.reconciliations_required_many, it.size.toString())
            }
        }
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                userClick = saveActiveReconciliationInteractor.saveActiveReconiliation::subscribe
            )
        )
}