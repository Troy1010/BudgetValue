package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface IPlansDomain {
    val plans: Observable<List<Plan>>
    val intentDeletePlan: PublishSubject<Plan>
}