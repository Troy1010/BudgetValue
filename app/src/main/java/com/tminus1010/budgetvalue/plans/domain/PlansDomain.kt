package com.tminus1010.budgetvalue.plans.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlansDomain @Inject constructor(
    private val plansRepo: IPlansRepo
) : ViewModel(), IPlansDomain {
    override val plans = plansRepo.plans
    override val intentDeletePlan = PublishSubject.create<Plan>()
        .also { it.launch { plansRepo.delete(it) } }
}