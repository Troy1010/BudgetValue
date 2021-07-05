package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
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
    private val plansRepo: IPlansRepo,
) : ViewModel() {
    // # Intents
    fun pushExpectedIncome(s: String) {
        activePlanDomain.activePlan
            .flatMapCompletable { plansRepo.updatePlanAmount(it, s.toMoneyBigDecimal()) }
            .observe(disposables)
    }

    fun pushActivePlanCA(category: Category, s: String) {
        activePlanDomain.activePlan
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