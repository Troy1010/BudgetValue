package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SaveActiveReconciliation @Inject constructor(
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
) {
    suspend operator fun invoke(reconciliationToDo: ReconciliationToDo) {
        reconciliationsRepo.push(
            Reconciliation(
                when (reconciliationToDo) {
                    is ReconciliationToDo.Anytime,
                    is ReconciliationToDo.Accounts,
                    -> LocalDate.now()
                    is ReconciliationToDo.PlanZ ->
                        reconciliationToDo.plan.localDatePeriod.midDate
                },
                activeReconciliationInteractor.defaultAmount.first(),
                activeReconciliationRepo.activeReconciliationCAs.first(),
            )
        )
        activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts())
    }
}