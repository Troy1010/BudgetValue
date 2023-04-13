package com.tminus1010.buva.ui.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.ui.budget.budget.BudgetFrag
import com.tminus1010.buva.ui.budget.plan.PlanFrag
import com.tminus1010.buva.ui.budget.reconciliation.ReconciliationHostFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BudgetHostVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        selectedBudgetHostPage.set(id)
    }

    // # Private
    init {
        savedStateHandle.get<Int>(KEY1)?.also { selectedBudgetHostPage.set(it) }
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