package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.extensions.withLatestFrom2
import com.tminus1010.budgetvalue._core.middleware.nullIfZero
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.plans.domain.IActivePlanDomain
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val activePlanDomain: ActivePlanDomain,
    private val plansRepo: IPlansRepo,
) : ViewModel() {
    val intentPushActivePlanCA: PublishSubject<Pair<Category, BigDecimal?>> = TODO()

    // # State
    val defaultAmount: LiveData<String> = activePlanDomain.defaultAmount
        .map { it.toString() }
        .toLiveData(errorSubject)
    val expectedIncome: LiveData<String> = activePlanDomain.expectedIncome
        .map { it.toString() }
        .toLiveData(errorSubject)
    val activePlanCAs: Observable<Map<String, LiveData<String>>> = activePlanDomain.activePlanCAs
        .map { it.associate { it.key.name to it.value.map { it.toString() }.toLiveData(errorSubject) } }
    // # Intents
    fun pushExpectedIncome(s: String) {
        activePlanDomain.activePlan.launch { plansRepo.updatePlanAmount(it, s.toMoneyBigDecimal()) }
    }
    fun pushActivePlanCA(category: Category, s: String) {
        activePlanDomain.activePlan.launch { plansRepo.updatePlanCA(it, category, s.toMoneyBigDecimal()) }
    }
}