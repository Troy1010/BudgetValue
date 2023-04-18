package com.tminus1010.buva.ui.budget.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.InvalidStateException
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.*
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.budget.reconciliation.accounts_reconciliation.AccountsReconciliationFrag
import com.tminus1010.buva.ui.budget.reconciliation.plan_reconciliation.PlanReconciliationFrag
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val activeAccountsReconciliationInteractor: ActiveAccountsReconciliationInteractor,
    private val showToast: ShowToast,
    private val throbberSharedVM: ThrobberSharedVM,
    private val activePlanReconciliationInteractor: ActivePlanReconciliationInteractor,
    private val activePlanRepo: ActivePlanRepo,
    private val userCategories: UserCategories,
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
            block = {
                when (reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
                    is ReconciliationToDo.PlanZ ->
                        activePlanReconciliationInteractor.save()
                    else ->
                        activeAccountsReconciliationInteractor.save()
                }
            },
        )
            .use(throbberSharedVM)
    }

    fun userReset() {
        when (reconciliationsToDoInteractor.currentReconciliationToDo.value) {
            is ReconciliationToDo.PlanZ ->
                GlobalScope.launch { activePlanReconciliationInteractor.reset() }.use(throbberSharedVM)
            else ->
                GlobalScope.launch { activeAccountsReconciliationInteractor.reset() }.use(throbberSharedVM)
        }
    }

    fun userResolve() {
        when (reconciliationsToDoInteractor.currentReconciliationToDo.value) {
            is ReconciliationToDo.PlanZ ->
                GlobalScope.launch { activePlanReconciliationInteractor.resolve() }.use(throbberSharedVM)
            else ->
                GlobalScope.launch { activeAccountsReconciliationInteractor.resolve() }.use(throbberSharedVM)
        }
    }

    fun userFillIntoCategory(category: Category) {
        when (reconciliationsToDoInteractor.currentReconciliationToDo.value) {
            is ReconciliationToDo.PlanZ ->
                GlobalScope.launch { activePlanReconciliationInteractor.fillIntoCategory(category) }.use(throbberSharedVM)
            else ->
                GlobalScope.launch { activeAccountsReconciliationInteractor.fillIntoCategory(category) }.use(throbberSharedVM)
        }
    }

    // # Private
    init {
        // Requirement: Whenever there's a new currentReconiliation, or the user changed something relevant, reset.
        //      There might be a better way to do this?
        GlobalScope.launch {
            merge(
                reconciliationsToDoInteractor.currentReconciliationToDo.drop(1),
                reconciliationsToDoInteractor.currentReconciliationToDo
                    .flatMapLatest {
                        when (it) {
                            is ReconciliationToDo.PlanZ ->
                                activePlanRepo.activePlan
                            else ->
                                flowOf()
                        }
                    },
            )
                .debounce(1000) // TODO: This is not ideal.
                .collect { userReset() }
        }
    }

    // # State
    val fragFactory =
        reconciliationsToDoInteractor.currentReconciliationToDo
            .map {
                when (it) {
                    is ReconciliationToDo.PlanZ -> {
                        { PlanReconciliationFrag.create(it) }
                    }
                    is ReconciliationToDo.Accounts,
                    is ReconciliationToDo.Anytime,
                    -> {
                        { AccountsReconciliationFrag() }
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
        userCategories.firstUnlimited.map { firstUnlimitedCategory ->
            listOfNotNull(
                ButtonVMItem(
                    title = "Reset",
                    onClick = ::userReset,
                ),
                ButtonVMItem(
                    title = "Resolve",
                    onClick = ::userResolve,
                ),
                if (firstUnlimitedCategory != null)
                    ButtonVMItem(
                        title = "Fill Into Unlimited",
                        onClick = { userFillIntoCategory(firstUnlimitedCategory) },
                    )
                else null,
                ButtonVMItem(
                    title = "Save",
                    onClick = ::userSave
                )
            )
        }
}
