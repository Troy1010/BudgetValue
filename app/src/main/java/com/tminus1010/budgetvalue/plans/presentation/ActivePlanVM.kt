package com.tminus1010.budgetvalue.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val activePlanRepo: ActivePlanRepo,
    private val plansRepo: PlansRepo,
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        activePlanRepo.activePlan
            .take(1)
            .flatMapCompletable { Rx.completableFromSuspend { plansRepo.updatePlanAmount(it, s.toMoneyBigDecimal()) } }
            .observe(disposables)
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        activePlanRepo.activePlan
            .take(1)
            .flatMapCompletable { Rx.completableFromSuspend { plansRepo.updatePlanCategoryAmount(it, category, s.toMoneyBigDecimal()) } }
            .observe(disposables)
    }

    // # Presentation State
    val defaultAmount = activePlanRepo.defaultAmount
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val expectedIncome = activePlanRepo.expectedIncome
        .map { it.toString() }
        .toState(disposables, errorSubject)
    val activePlanCAs: Observable<Map<Category, Observable<String>>> =
        activePlanRepo.activePlanCAs
            .map { it.mapValues { it.value.map { it.toString() } } }
}