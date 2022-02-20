package com.tminus1010.budgetvalue.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.app.interactor.SetActivePlanFromHistoryInteractor
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [PlanVM] represents the [ActivePlan] of [ActivePlanRepo]
 */
@HiltViewModel
class PlanVM @Inject constructor(
    private val activePlanRepo: ActivePlanRepo,
    private val setActivePlanFromHistoryInteractor: SetActivePlanFromHistoryInteractor,
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        GlobalScope.launch { activePlanRepo.updateTotal(s.toMoneyBigDecimal()) }
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userSetActivePlanFromHistory() {
        GlobalScope.launch { setActivePlanFromHistoryInteractor.setActivePlanFromHistory() }
    }

    // # State
    val defaultAmount =
        activePlanRepo.activePlan
            .map { it.defaultAmount.toString() }
            // TODO: How to divert errors..?
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val expectedIncome =
        activePlanRepo.activePlan
            .map { it.total.toString() }
            // TODO: How to divert errors..?
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val activePlanCAs =
        activePlanRepo.activePlan
            .map { it.categoryAmounts.mapValues { it.value.toString() } }
            // TODO: How to divert errors..?
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    "Set from history",
                    onClick = { userSetActivePlanFromHistory() },
                )
            )
        )
}