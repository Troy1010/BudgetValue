package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlansVM @Inject constructor(
    private val plansRepo: IPlansRepo
) : ViewModel() {
    // # Intents
    fun deletePlan(plan: Plan) {
        plansRepo.delete(plan)
            .observe(disposables)
    }
}