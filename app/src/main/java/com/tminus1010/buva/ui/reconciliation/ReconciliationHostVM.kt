package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.*
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val saveActiveReconciliation: SaveActiveReconciliation,
    private val budgetedForActiveReconciliationInteractor: BudgetedForActiveReconciliationInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationInteractor2: ActiveReconciliationInteractor2,
    private val showToast: ShowToast,
    private val matchBudgetedForActiveReconciliation: MatchBudgetedForActiveReconciliation,
    private val activePlanRepo: ActivePlanRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val throbberSharedVM: ThrobberSharedVM,
    private val reconciliationSkipInteractor: ReconciliationSkipInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val categoryInteractor: CategoryInteractor,
    private val accountsRepo: AccountsRepo,
    private val transactionsInteractor: TransactionsInteractor,
    private val planReconciliationInteractor: PlanReconciliationInteractor,
) : ViewModel() {
    // # User Intents
    fun userSave() {
//        if (
//            !budgetedForActiveReconciliationInteractor.categoryAmountsAndTotal.value!!.isAllValid
//            || (
//                    activeReconciliationInteractor.categoryAmountsAndTotal.value!!.categoryAmounts.isEmpty()
//                            && activeReconciliationInteractor.categoryAmountsAndTotal.value!!.defaultAmount.isZero
//                    )
//        )
//            showToast(NativeText.Simple("Invalid input"))
//        else
        GlobalScope.launch { saveActiveReconciliation(reconciliationsToDoInteractor.currentReconciliationToDo.value!!) }
            .use(throbberSharedVM)
    }

    // TODO: Given a plan reconciliation in the future and no account reconciliation, this does not work as expected.
    fun userEqualizeActiveReconciliation() {
        GlobalScope.launch { matchBudgetedForActiveReconciliation() }
            .use(throbberSharedVM)
    }

    fun userUseActivePlan() {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmounts(activePlanRepo.activePlan.first().categoryAmounts) }
    }

    fun userClearActiveReconciliation() {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts()) }
    }

    fun userMatch() {
        val x = reconciliationsToDoInteractor.currentReconciliationToDo.value as ReconciliationToDo.PlanZ
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts(x.transactionBlock.categoryAmounts.mapValues { -it.value })) }
    }

    fun userMatchUp() {
        when (val x = reconciliationsToDoInteractor.currentReconciliationToDo.value) {
            is ReconciliationToDo.PlanZ -> GlobalScope.launch { planReconciliationInteractor.matchUp() }.use(throbberSharedVM)
            else -> error("Unhandled type:$x")
        }
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
        reconciliationsToDoInteractor.currentReconciliationToDo.map {
            listOfNotNull(
                if (it is ReconciliationToDo.Accounts)
                    ButtonVMItem(
                        title = "Equalize",
                        onClick = ::userEqualizeActiveReconciliation,
                    )
                else null,
                ButtonVMItem(
                    title = "Clear",
                    onClick = ::userClearActiveReconciliation,
                ),
                if (it is ReconciliationToDo.PlanZ)
                    ButtonVMItem(
                        title = "Use Plan",
                        onClick = ::userUseActivePlan,
                    )
                else null,
                if (it is ReconciliationToDo.PlanZ)
                    ButtonVMItem(
                        title = "Match",
                        onClick = ::userMatch,
                    )
                else null,
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
