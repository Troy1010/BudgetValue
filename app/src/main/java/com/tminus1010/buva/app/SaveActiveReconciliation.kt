package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
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
                date = when (reconciliationToDo) {
                    is ReconciliationToDo.Anytime ->
                        LocalDate.now()
                    is ReconciliationToDo.Accounts ->
                        reconciliationToDo.date
                    is ReconciliationToDo.PlanZ ->
                        reconciliationToDo.plan.localDatePeriod.midDate
                },
                total = activeReconciliationInteractor.categoryAmountsAndTotal.first().total.logx("SaveTotal"),
//                defaultAmount = when (reconciliationToDo) {
//                    is ReconciliationToDo.Anytime,
//                    is ReconciliationToDo.Accounts,
//                    -> activeReconciliationRepo.activeReconciliationCAs.first().defaultAmount(BigDecimal.ZERO)
//                    is ReconciliationToDo.PlanZ ->
//                        error("Oh no!")
////                        activeReconciliationInteractor.defaultAmount.first()
//                },
                categoryAmounts = activeReconciliationRepo.activeReconciliationCAs.first(),
            )
        )
        activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts())
    }
}