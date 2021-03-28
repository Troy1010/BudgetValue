package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.budgeted.domain.IBudgetedDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BudgetedVM @Inject constructor(
    budgetedDomain: BudgetedDomain
): ViewModel(), IBudgetedDomain by budgetedDomain