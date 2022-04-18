package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Reconciliation
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SaveActiveReconciliation @Inject constructor(
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
) {
    suspend operator fun invoke() {
        reconciliationsRepo.push(
            Reconciliation(
                LocalDate.now(),
                activeReconciliationInteractor.defaultAmount.first(),
                activeReconciliationRepo.activeReconciliationCAs.first(),
            )
        )
        activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts())
    }
}