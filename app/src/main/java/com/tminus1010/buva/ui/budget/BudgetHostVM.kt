package com.tminus1010.buva.ui.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.ui.all_features.ReadyToReconcilePresentationService
import com.tminus1010.buva.ui.all_features.isReady
import com.tminus1010.buva.ui.budget.budget.BudgetFrag
import com.tminus1010.buva.ui.budget.plan.PlanFrag
import com.tminus1010.buva.ui.budget.reconciliation.ReconciliationHostFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetHostVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
    private val readyToReconcilePresentationService: ReadyToReconcilePresentationService,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        when (id) {
            R.id.reconciliationHostFrag ->
                GlobalScope.launch {
                    readyToReconcilePresentationService.tryShowAlertDialog()
                }
            else ->
                selectedBudgetHostPage.set(id)
        }
    }

    // # Private
    init {
        savedStateHandle.get<Int>(KEY1)?.also { selectedBudgetHostPage.set(it) }
        // Requirement: Given selectedBudgetHostPage is reconcile and app is not readyToReconcile When user navigates to BudgetHost Then app navigates to a default page.
        if (selectedBudgetHostPage.flow.value == R.id.reconciliationHostFrag && !readyToReconcilePresentationService.isReady)
            selectedBudgetHostPage.set(R.id.planFrag)
    }

    // # State
    val selectedItemId = selectedBudgetHostPage.flow
    val fragFactory =
        selectedItemId.map {
            when (it) {
                R.id.planFrag -> {
                    { PlanFrag() }
                }
                R.id.reconciliationHostFrag -> {
                    { ReconciliationHostFrag() }
                }
                R.id.budgetFrag -> {
                    { BudgetFrag() }
                }
                else -> error("Unknown id")
            }
        }
}