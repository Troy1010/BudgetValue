package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all_layers.extensions.isZero
import com.tminus1010.budgetvalue._core.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.UnformattedString
import com.tminus1010.budgetvalue.reconcile.app.convenience_service.ReconciliationsToDoUC
import com.tminus1010.budgetvalue.reconcile.app.interactor.ActiveReconciliationInteractor
import com.tminus1010.budgetvalue.reconcile.app.interactor.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.budgetvalue.reconcile.app.interactor.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue.reconcile.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.rx.extensions.value
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
            !budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal.value!!.isAllValid.logx("aaa")
            || (
                    activeReconciliationInteractor.categoryAmountsAndTotal.value!!.categoryAmounts.isEmpty().logx("bbb")
                            && activeReconciliationInteractor.categoryAmountsAndTotal.value!!.defaultAmount.isZero.logx("ccc")
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
                null -> "Anytime Reconciliation"
            }.let { UnformattedString(it) }
        }
    val subTitle =
        reconciliationsToDoUC.map {
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
                onClick = ::userSave
            )
        )
}