package com.tminus1010.budgetvalue.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo3
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [PlanVM] represents the activePlan of [ActivePlanRepo3]
 */
@HiltViewModel
class PlanVM @Inject constructor(
    private val activePlanRepo: ActivePlanRepo3,
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        GlobalScope.launch { activePlanRepo.updateTotal(s.toMoneyBigDecimal()) }
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    // # Presentation State
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
}