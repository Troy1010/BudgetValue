package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.InvalidStateException
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.ActiveReconciliationInteractor
import com.tminus1010.buva.app.PlanReconciliationInteractor
import com.tminus1010.buva.app.ReconciliationsToDoInteractor
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val showToast: ShowToast,
    private val throbberSharedVM: ThrobberSharedVM,
    private val planReconciliationInteractor: PlanReconciliationInteractor,
) : ViewModel() {
    // # User Intents
    fun userSave() {
        GlobalScope.launch(
            context = onError {
                when (it) {
                    is InvalidStateException -> showToast("Invalid input")
                    else -> throw Exception(it)
                }
            },
            block = { activeReconciliationInteractor.save() },
        )
            .use(throbberSharedVM)
    }

    fun userResetActiveReconciliation() {
        GlobalScope.launch { activeReconciliationInteractor.reset() }
            .use(throbberSharedVM)
    }

    fun userMatchUp() {
        when (val x = reconciliationsToDoInteractor.currentReconciliationToDo.value) {
            is ReconciliationToDo.PlanZ -> GlobalScope.launch { planReconciliationInteractor.matchUp() }.use(throbberSharedVM)
            else -> error("Unhandled type:$x")
        }
    }

    // # State
    val currentReconciliationToDo =
        reconciliationsToDoInteractor.currentReconciliationToDo
            .map {
                when (it) {
                    is ReconciliationToDo.PlanZ -> {
                        { PlanReconciliationSubFrag.create(it) }
                    }
                    is ReconciliationToDo.Accounts,
                    is ReconciliationToDo.Anytime,
                    -> {
                        { AccountsReconciliationSubFrag() }
                    }
                }
            }
    val title =
        reconciliationsToDoInteractor.currentReconciliationToDo
            .map {
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
        reconciliationsToDoInteractor.currentReconciliationToDo.map {
            listOfNotNull(
                ButtonVMItem(
                    title = "Reset",
                    onClick = ::userResetActiveReconciliation,
                ),
                if (it is ReconciliationToDo.PlanZ)
                    ButtonVMItem(
                        title = "Match Up",
                        onClick = ::userMatchUp,
                    )
                else null,
                ButtonVMItem(
                    title = "Save",
                    onClick = ::userSave
                )
            )
        }
}
