package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.plans.domain.IActivePlanDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivePlanVM @Inject constructor(
    activePlanDomain: ActivePlanDomain
) : ViewModel(), IActivePlanDomain by activePlanDomain