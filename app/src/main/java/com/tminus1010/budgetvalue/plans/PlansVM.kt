package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.plans.domain.IPlansDomain
import com.tminus1010.budgetvalue.plans.domain.PlansDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlansVM @Inject constructor(
    plansDomain: PlansDomain
) : ViewModel(), IPlansDomain by plansDomain