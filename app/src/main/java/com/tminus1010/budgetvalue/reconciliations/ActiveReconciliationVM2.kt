package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain2
import com.tminus1010.budgetvalue.reconciliations.domain.IActiveReconciliationDomain2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Separate from ActiveReconciliationVM to avoid circular dependency graph
@HiltViewModel
class ActiveReconciliationVM2 @Inject constructor(
    activeReconciliationDomain2: ActiveReconciliationDomain2
) : ViewModel(), IActiveReconciliationDomain2 by activeReconciliationDomain2