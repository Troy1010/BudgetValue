package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.easyEquals
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // TODO: Only using Singleton to avoid excessive leaks while using GlobalScope without any disposal strategy.
class PlanReconciliationInteractor @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val historyInteractor: HistoryInteractor,
) {
    suspend fun matchUp() {
        val CAs = activeReconciliationCAsAndTotal.first().categoryAmounts
        val budgetedCAsWithFlippedSign = summedRelevantHistory.first().categoryAmounts.mapValues { -it.value }
        activeReconciliationRepo.pushCategoryAmounts(
            CAs.maxTogether(budgetedCAsWithFlippedSign)
        )
    }

    suspend fun fillIntoCategory(category: Category) {
        val date =
            when (val x = reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
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

    private val summedRelevantHistory =
        combine(reconciliationsToDoInteractor.currentReconciliationToDo.filterIsInstance<ReconciliationToDo.PlanZ>(), historyInteractor.entireHistory)
        { currentReconciliationToDo, entireHistory ->
            CategoryAmountsAndTotal.addTogether(
                entireHistory.filter {
                    when (it) {
                        is Reconciliation -> it.date < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                        is TransactionBlock -> it.datePeriod!!.startDate < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                        else -> true
                    }
                }
            )
        }
            // TODO: GlobalScope without any disposal strategy is not ideal.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    // TODO: This is a quasi-redefinition of ActiveReconciliationInteractor.categoryAmountsAndTotal
    val activeReconciliationCAsAndTotal =
        activeReconciliationRepo.activeReconciliationCAs.map { activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = BigDecimal.ZERO,
            )
        }
            // TODO: GlobalScope without any disposal strategy is not ideal.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val budgeted =
        combine(activeReconciliationCAsAndTotal, summedRelevantHistory)
        { activeReconciliation, relevantHistory ->
            CategoryAmountsAndTotalWithValidation(
                CategoryAmountsAndTotal.addTogether(
                    activeReconciliation,
                    relevantHistory,
                ),
                caValidation = { true },
                defaultAmountValidation = { (it ?: BigDecimal.ZERO).easyEquals(BigDecimal.ZERO) },
            )
        }
            // TODO: GlobalScope without any disposal strategy is not ideal.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val targetDefaultAmount =
        reconciliationsToDoInteractor.currentReconciliationToDo.filterIsInstance<ReconciliationToDo.PlanZ>().map { currentReconciliationToDo ->
            -currentReconciliationToDo.transactionBlock.incomeBlock.total
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}