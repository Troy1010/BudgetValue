package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items.UnformattedString
import com.tminus1010.budgetvalue.all.app.interactors.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue.all.app.interactors.individual.ReconciliationsToDo
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
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
                is ReconciliationToDo.Anytime -> "Anytime Reconciliation"
                null -> ""
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

    // An Accounts reconciliation needs:
    // the reconciliation, with default calculated by accounts total - something?
    // the budgeted (Is this necessary?)
    // A Plan reconciliation needs:
    // the plan
    // the actual
    // the reconciliation, with default calculated by plan total - actual total
    // the budgeted (Is this necessary?)
    // An Anytime reconciliation needs:
    // the reconciliation, with 0 as default amount
    // the budgeted

    // A grid of Active / Plan / Reconciliation / Budgeted
    // A Save button


//    // # Internal
//    private fun mapReconciliationToDosToButtonVMItems(reconciliationToDos: List<ReconciliationToDo>): List<ButtonVMItem> {
//        return reconciliationToDos
//            .map {
//                when (it) {
//                    is ReconciliationToDo.Accounts -> ButtonVMItem(
//                        title = "Accounts Reconciliation",
//                        userClick = navToAccountsReconciliation::emit,
//                    )
//                    is ReconciliationToDo.Anytime -> ButtonVMItem(
//                        title = "Anytime Reconciliation",
//                        userClick = navToAnytimeReconciliation::emit
//                    )
//                    is ReconciliationToDo.PlanZ -> TODO()
//                }
//            }
//    }
//
//    // # Presentation Output
//    // ## Events
//    val navToAccountsReconciliation = PublishSubject.create<Unit>()!!
//    val navToPlanReconciliation = PublishSubject.create<Unit>()!!
//    val navToAnytimeReconciliation = PublishSubject.create<Unit>()!!
//
//    // ## State
//    val buttons =
//        ReconciliationsToDoInteractor.reconciliationsToDo
//            .map(::mapReconciliationToDosToButtonVMItems)!!
//
//    val title =
//        ReconciliationsToDoInteractor.reconciliationsToDo
//            .map {
//                when (it.size) {
//                    0 -> UnformattedString(R.string.reconciliations_required_none)
//                    1 -> UnformattedString(R.string.reconciliations_required_one)
//                    else -> UnformattedString(R.string.reconciliations_required_many, it.size.toString())
//                }
//            }!!
}