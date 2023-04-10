package com.tminus1010.buva.ui.budget

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.budget.plan.PlanFrag
import com.tminus1010.buva.ui.budget.reconciliation.ReconciliationHostFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BudgetHostVM @Inject constructor(
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        when (id) {
            R.id.planFrag ->
                fragFactory.onNext { PlanFrag() }
            R.id.reconciliationHostFrag ->
                fragFactory.onNext { ReconciliationHostFrag() }
            else -> error("Unknown id")
        }
    }

    // # State
    val fragFactory = MutableStateFlow<() -> Fragment>(value = { PlanFrag() })
}