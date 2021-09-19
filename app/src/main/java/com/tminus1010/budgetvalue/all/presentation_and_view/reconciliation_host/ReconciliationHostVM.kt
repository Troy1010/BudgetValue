package com.tminus1010.budgetvalue.all.presentation_and_view.reconciliation_host

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items.UnformattedString
import com.tminus1010.budgetvalue.all.app.interactors.ReconciliationsToDoInteractor
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.all.framework.extensions.emit
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    ReconciliationsToDoInteractor: ReconciliationsToDoInteractor,
) : ViewModel() {
    // # Internal
    private fun mapReconciliationsToDoToButtonVMItem(reconciliationToDos: List<ReconciliationToDo>): List<ButtonVMItem> {
        return reconciliationToDos
            .map {
                when (it) {
                    is ReconciliationToDo.Accounts -> ButtonVMItem(
                        title = "Accounts Reconciliation",
                        onClick = navToAccountsReconciliation::emit,
                    )
                    is ReconciliationToDo.PlanZ -> TODO()
                }
            }
    }

    // # Presentation Output
    // ## Events
    val navToAccountsReconciliation = PublishSubject.create<Unit>()!!
    val navToPlanReconciliation = PublishSubject.create<Unit>()!!

    // ## State
    val buttons =
        ReconciliationsToDoInteractor.reconciliationsToDo
            .map(::mapReconciliationsToDoToButtonVMItem)!!

    val title =
        ReconciliationsToDoInteractor.reconciliationsToDo
            .map {
                when (it.size) {
                    0 -> UnformattedString(R.string.reconciliations_required_none)
                    1 -> UnformattedString(R.string.reconciliations_required_one)
                    else -> UnformattedString(R.string.reconciliations_required_many, it.size.toString())
                }
            }!!
}