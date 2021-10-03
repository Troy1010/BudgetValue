package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val activePlanDomain: ActivePlanDomain,
    private val plansRepo: PlansRepo,
) : ViewModel() {
    // # Input
    fun userSaveExpectedIncome(s: String) {
        activePlanDomain.activePlan
            .take(1)
            .flatMapCompletable { plansRepo.updatePlanAmount(it, s.toMoneyBigDecimal()) }
            .observe(disposables)
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        activePlanDomain.activePlan
            .take(1)
            .flatMapCompletable { plansRepo.updatePlanCA(it, category, s.toMoneyBigDecimal()) }
            .observe(disposables)
    }

    // # Output
    val defaultAmount = activePlanDomain.defaultAmount
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val expectedIncome = activePlanDomain.expectedIncome
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val activePlanCAs: Observable<Map<Category, Observable<String>>> = activePlanDomain.activePlanCAs
        .map { it.mapValues { it.value.map { it.toString() } } }
}