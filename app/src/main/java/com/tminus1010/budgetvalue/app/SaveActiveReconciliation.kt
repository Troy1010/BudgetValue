package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor.ActiveReconciliationDefaultAmountInteractor
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Reconciliation
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SaveActiveReconciliation @Inject constructor(
    private val activeReconciliationDefaultAmountInteractor: ActiveReconciliationDefaultAmountInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
) {
    suspend operator fun invoke() {
        reconciliationsRepo.push(
            Reconciliation(
                LocalDate.now(),
                activeReconciliationDefaultAmountInteractor.activeReconciliationDefaultAmount.first(),
                activeReconciliationRepo.activeReconciliationCAs.first(),
            )
        )
        activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts())
    }
}