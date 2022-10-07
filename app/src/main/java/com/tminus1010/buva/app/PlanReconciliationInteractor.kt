package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.easyEquals
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

class PlanReconciliationInteractor @Inject constructor(
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val historyInteractor: HistoryInteractor,
) {
    suspend fun fillIntoCategory(category: Category) {
        val date =
            when(val x = reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
                is ReconciliationToDo.PlanZ -> x.transactionBlock.datePeriod?.endDate
                else -> null
            }
        historyInteractor.entireHistory.first()
            .filter {
                if (date == null)
                    true
                else
                    when (it) {
                        is Reconciliation -> it.date < date
                        is TransactionBlock -> it.datePeriod!!.startDate < date
                        else -> true
                    }
            }
            .addTogether()
            .categoryAmounts
            .plus(activeReconciliationRepo.activeReconciliationCAs.first())
            .calcFillAmount(fillCategory = category, total = BigDecimal.ZERO)
            .also { activeReconciliationRepo.pushCategoryAmount(category, it) }
    }

    val budgeted =
        combine(activeReconciliationInteractor.categoryAmountsAndTotal, reconciliationsToDoInteractor.currentReconciliationToDo.filterIsInstance<ReconciliationToDo.PlanZ>(), historyInteractor.entireHistory)
        { activeReconciliation, currentReconciliationToDo, entireHistory ->
            CategoryAmountsAndTotalWithValidation(
                CategoryAmountsAndTotal.addTogether(
                    activeReconciliation,
                    *entireHistory.filter {
                        when (it) {
                            is Reconciliation -> it.date < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                            is TransactionBlock -> it.datePeriod!!.startDate < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                            else -> true
                        }
                    }.toTypedArray(),
                ),
                caValidation = { true },
                defaultAmountValidation = { (it ?: BigDecimal.ZERO).easyEquals(BigDecimal.ZERO) },
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}