package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.UnformattedString
import com.tminus1010.budgetvalue.reconcile.app.interactor.ReconciliationsToDoInteractor
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.all.framework.extensions.emit
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM_OLD @Inject constructor(
    ReconciliationsToDoInteractor: ReconciliationsToDoInteractor,
) : ViewModel() {
    // # Internal
    private fun mapReconciliationToDosToButtonVMItems(reconciliationToDos: List<ReconciliationToDo>): List<ButtonVMItem> {
        return reconciliationToDos
            .map {
                when (it) {
                    is ReconciliationToDo.Accounts -> ButtonVMItem(
                        title = "Accounts Reconciliation",
                        userClick = navToAccountsReconciliation::emit,
                    )
                    is ReconciliationToDo.Anytime -> ButtonVMItem(
                        title = "Anytime Reconciliation",
                        userClick = navToAnytimeReconciliation::emit
                    )
                    is ReconciliationToDo.PlanZ -> TODO()
                }
            }
    }

    // # Presentation Output
    // ## Events
    val navToAccountsReconciliation = PublishSubject.create<Unit>()!!
    val navToPlanReconciliation = PublishSubject.create<Unit>()!!
    val navToAnytimeReconciliation = PublishSubject.create<Unit>()!!

    // ## State
    val buttons =
        ReconciliationsToDoInteractor.reconciliationsToDo
            .map(::mapReconciliationToDosToButtonVMItems)!!

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