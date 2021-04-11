package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlansVM @Inject constructor(
    private val plansRepo: IPlansRepo
) : ViewModel() {
    // # Intents
    fun deletePlan(plan: Plan) {
        Rx.launch { plansRepo.delete(plan) }
    }
}