package com.tminus1010.budgetvalue.plans.domain

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.models.Plan
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

interface IActivePlanDomain {
    val activePlan: BehaviorSubject<Plan>
    val activePlanCAs: Observable<Map<Category, BehaviorSubject<BigDecimal>>>
    val expectedIncome: Observable<BigDecimal>
    val defaultAmount: Observable<BigDecimal>
}