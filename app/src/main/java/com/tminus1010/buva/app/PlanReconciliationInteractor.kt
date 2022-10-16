package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.InvalidStateException
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
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
    private val reconciliationsRepo: ReconciliationsRepo,
    private val activePlanInteractor: ActivePlanInteractor,
) {
    suspend fun save() {
        if (!budgeted.first().isAllValid) throw InvalidStateException()
        val reconciliationToDo = reconciliationsToDoInteractor.currentReconciliationToDo.first() as ReconciliationToDo.PlanZ
        val casAndTotalToPush =
            CategoryAmountsAndTotal.addTogether(
                activeReconciliationCAsAndTotal.first(),
                activePlanInteractor.activePlan.first()
            )
        reconciliationsRepo.push(
            Reconciliation(
                date = reconciliationToDo.transactionBlock.datePeriod!!.midDate,
                total = casAndTotalToPush.total,
                categoryAmounts = casAndTotalToPush.categoryAmounts,
            )
        )
    }

    suspend fun matchUp() {
        activeReconciliationRepo.pushCategoryAmounts(
            activeReconciliationRepo.activeReconciliationCAs.first()
                .zipTogether(budgeted.first().categoryAmounts.filter { it.value <= BigDecimal.ZERO })
                { _, b -> -b }
        )
    }

    // # Internal
    private val summedRelevantHistory =
        combine(reconciliationsToDoInteractor.currentReconciliationToDo.filterIsInstance<ReconciliationToDo.PlanZ>(), historyInteractor.entireHistory)
        { currentReconciliationToDo, entireHistory ->
            entireHistory.filter {
                when (it) {
                    is Reconciliation -> it.date < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                    is TransactionBlock -> it.datePeriod!!.startDate < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                    else -> true
                }
            }
                .addTogether()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1) // TODO: GlobalScope without any disposal strategy is not ideal.

    // TODO: This is a quasi-redefinition of ActiveReconciliationInteractor.categoryAmountsAndTotal
    val activeReconciliationCAsAndTotal =
        activeReconciliationRepo.activeReconciliationCAs.map { activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = BigDecimal.ZERO,
            )
        }
            // TODO: GlobalScope without any disposal strategy is not ideal.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1) // TODO: GlobalScope without any disposal strategy is not ideal.

    val budgeted =
        combine(activeReconciliationCAsAndTotal, summedRelevantHistory, activePlanInteractor.activePlan)
        { activeReconciliation, relevantHistory, activePlan ->
            CategoryAmountsAndTotalWithValidation(
                CategoryAmountsAndTotal.addTogether(
                    activeReconciliation,
                    relevantHistory,
                    activePlan,
                ),
                caValidation = { (it ?: BigDecimal.ZERO) >= BigDecimal.ZERO },
                defaultAmountValidation = { true },
            )
        }
            // TODO: GlobalScope without any disposal strategy is not ideal.
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val targetDefaultAmount =
        summedRelevantHistory.map { summedRelevantHistory ->
            -summedRelevantHistory.defaultAmount
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1) // TODO: GlobalScope without any disposal strategy is not ideal.
}