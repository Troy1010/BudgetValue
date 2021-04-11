package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
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
    // # State
    val defaultAmount: LiveData<String> = activePlanDomain.defaultAmount
        .map { it.toString() }
        .toLiveData(errorSubject)
    val expectedIncome: LiveData<String> = activePlanDomain.expectedIncome
        .map { it.toString() }
        .toLiveData(errorSubject)
    val activePlanCAs: Observable<Map<Category, LiveData<String>>> = activePlanDomain.activePlanCAs
        .map { it.mapValues { it.value.map { it.toString() }.toLiveData(errorSubject) } }

    // # Intents
    fun pushExpectedIncome(s: String) {
        activePlanDomain.activePlan.take(1).launch { plansRepo.updatePlanAmount(it, s.toMoneyBigDecimal()) }
    }

    fun pushActivePlanCA(category: Category, s: String) {
        activePlanDomain.activePlan.take(1).launch { plansRepo.updatePlanCA(it, category, s.toMoneyBigDecimal()) }
    }
}