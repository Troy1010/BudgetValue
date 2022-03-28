package com.tminus1010.budgetvalue._unrestructured.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.ActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.ReconciliationsToDoInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val saveActiveReconciliationInteractor: SaveActiveReconciliationInteractor,
    private val budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val showToast: ShowToast,
) : ViewModel() {
    // # User Intents
    fun userSave() {
        if (
            !budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal.value!!.isAllValid.logx("isAllValid")
            || (
                    activeReconciliationInteractor.categoryAmountsAndTotal.value!!.categoryAmounts.isEmpty().logx("categoryAmounts.isEmpty()")
                            && activeReconciliationInteractor.categoryAmountsAndTotal.value!!.defaultAmount.isZero.logx("defaultAmount.isZero")
                    )
        )
            showToast(NativeText.Simple("Invalid input"))
        else
            saveActiveReconciliationInteractor.saveActiveReconciliation.subscribe()
    }

    // # State
    val currentReconciliationToDo = reconciliationsToDoInteractor.currentReconciliationToDo
    val title =
        currentReconciliationToDo.map {
            NativeText.Simple(
                when (it) {
                    is ReconciliationToDo.Accounts -> "Accounts Reconciliation"
                    is ReconciliationToDo.PlanZ -> "Plan Reconciliation"
                    is ReconciliationToDo.Anytime,
                    null,
                    -> "Anytime Reconciliation"
                }
            )
        }
    val subTitle = reconciliationsToDoInteractor.reconciliationsToDo.map { NativeText.Plural(R.plurals.reconciliations_required, it.size, it.size) }
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                onClick = ::userSave
            )
        )
}