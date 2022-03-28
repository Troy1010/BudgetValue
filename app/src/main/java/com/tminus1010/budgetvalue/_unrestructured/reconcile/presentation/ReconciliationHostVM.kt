package com.tminus1010.budgetvalue._unrestructured.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.convenience_service.ReconciliationsToDoUC
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.ActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    reconciliationsToDoUC: ReconciliationsToDoUC,
    private val saveActiveReconciliationInteractor: SaveActiveReconciliationInteractor,
    private val budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
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
            toast.onNext("Invalid input")
        else
            saveActiveReconciliationInteractor.saveActiveReconciliation.subscribe()
    }

    // # Presentation Events
    val toast = PublishSubject.create<String>()

    // # State
    val currentReconciliationToDo =
        reconciliationsToDoUC.mapBox { it.firstOrNull() }
    val title =
        currentReconciliationToDo.map { (it) ->
            when (it) {
                is ReconciliationToDo.Accounts -> "Accounts Reconciliation"
                is ReconciliationToDo.PlanZ -> "Plan Reconciliation"
                is ReconciliationToDo.Anytime,
                null,
                -> "Anytime Reconciliation"
            }.let { NativeText.Simple(it) }
        }
    val subTitle = reconciliationsToDoUC.map { NativeText.Plural(R.plurals.reconciliations_required, it.size, it.size) }
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                onClick = ::userSave
            )
        )
}