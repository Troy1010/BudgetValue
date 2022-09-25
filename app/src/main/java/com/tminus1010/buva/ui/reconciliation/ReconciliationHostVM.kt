package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.*
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val saveActiveReconciliation: SaveActiveReconciliation,
    private val budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val showToast: ShowToast,
    private val equalizeActiveReconciliation: EqualizeActiveReconciliation,
    private val activePlanRepo: ActivePlanRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val throbberSharedVM: ThrobberSharedVM,
    private val reconciliationSkipInteractor: ReconciliationSkipInteractor
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
            suspend { saveActiveReconciliation(reconciliationsToDoInteractor.currentReconciliationToDo.value!!) }
                .observe(GlobalScope)
                .use(throbberSharedVM)
    }

    fun userEqualizeActiveReconciliation() {
        suspend { equalizeActiveReconciliation() }
            .observe(GlobalScope)
            .use(throbberSharedVM)
    }

    fun userUseActivePlan() {
        activePlanRepo.activePlan
            .onEach { activeReconciliationRepo.pushCategoryAmounts(it.categoryAmounts) }
            .observe(GlobalScope)
    }

    fun userSkip() {
        val x = reconciliationsToDoInteractor.currentReconciliationToDo.value as ReconciliationToDo.PlanZ
        suspend { reconciliationSkipInteractor.push(x.plan.localDatePeriod.midDate) }
            .observe(GlobalScope)
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
                if (it is ReconciliationToDo.PlanZ)
                    ButtonVMItem(
                        title = "Use Active Plan",
                        onClick = ::userUseActivePlan,
                    )
                else null,
                if (it is ReconciliationToDo.PlanZ)
                    ButtonVMItem(
                        title = "Skip",
                        onClick = ::userSkip,
                    )
                else null,
                ButtonVMItem(
                    title = "Save",
                    onClick = ::userSave
                )
            )
        }
}
