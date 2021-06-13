package com.tminus1010.budgetvalue.plans

import com.tminus1010.budgetvalue._core.BaseViewModel
import com.tminus1010.budgetvalue._core.extensions.await
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val activePlanDomain: ActivePlanDomain,
    private val plansRepo: IPlansRepo,
) : BaseViewModel() {
    // # State
    val defaultAmount = activePlanDomain.defaultAmount
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val expectedIncome = activePlanDomain.expectedIncome
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val activePlanCAs: Observable<Map<Category, Observable<String>>> = activePlanDomain.activePlanCAs
        .map { it.mapValues { it.value.map { it.toString() } } }

    // # Intents
    fun pushExpectedIncome(s: String) {
        Rx.launch { plansRepo.updatePlanAmount(activePlanDomain.activePlan.await(), s.toMoneyBigDecimal()) }
    }

    fun pushActivePlanCA(category: Category, s: String) {
        Rx.launch { plansRepo.updatePlanCA(activePlanDomain.activePlan.await(), category, s.toMoneyBigDecimal()) }
    }
}