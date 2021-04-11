package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.domain.IActiveReconciliationDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActiveReconciliationVM @Inject constructor(
    private val reconciliationsRepo: IReconciliationsRepo,
    activeReconciliationDomain: ActiveReconciliationDomain,
) : ViewModel(), IActiveReconciliationDomain by activeReconciliationDomain {
    // # Intents
    fun pushActiveReconcileCA(category: Category, s: String) {
        Rx.launch { reconciliationsRepo.pushActiveReconciliationCA(category to s.toMoneyBigDecimal()) }
    }
}
