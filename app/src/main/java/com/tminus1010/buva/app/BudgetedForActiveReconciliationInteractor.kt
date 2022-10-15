package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.easyEquals
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.CategoryAmountsAndTotalWithValidation
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

class BudgetedForActiveReconciliationInteractor @Inject constructor(
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val categoryAmountsAndTotal =
        combine(activeReconciliationInteractor.activeReconciliationCAsAndTotal, reconciliationsToDoInteractor.currentReconciliationToDo, reconciliationsRepo.reconciliations, transactionsInteractor.transactionBlocks)
        { activeReconciliation, currentReconciliationToDo, reconciliations, transactionBlocks ->
            val relevantReconciliations =
                when (currentReconciliationToDo) {
                    is ReconciliationToDo.PlanZ ->
                        reconciliations.filter { it.date < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate }
                    is ReconciliationToDo.Accounts ->
                        reconciliations.filter { it.date < currentReconciliationToDo.date }
                    else -> reconciliations
                }
            val relevantTransactionBlocks =
                when (currentReconciliationToDo) {
                    is ReconciliationToDo.PlanZ ->
                        transactionBlocks.filter { it.datePeriod!!.startDate < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate }
                    is ReconciliationToDo.Accounts ->
                        transactionBlocks.filter { it.datePeriod!!.startDate < currentReconciliationToDo.date }
                    else -> transactionBlocks
                }
            CategoryAmountsAndTotalWithValidation(
                categoryAmounts = CategoryAmounts.addTogether(
                    activeReconciliation.categoryAmounts,
                    *relevantReconciliations.map { it.categoryAmounts }.toTypedArray(),
                    *relevantTransactionBlocks.map { it.categoryAmounts }.toTypedArray(),
                ),
                total = activeReconciliation.total
                    .plus(relevantReconciliations.map { it.total }.sum())
                    .plus(relevantTransactionBlocks.map { it.total }.sum()),
                caValidation = {
                    when (currentReconciliationToDo) {
                        is ReconciliationToDo.Accounts,
                        is ReconciliationToDo.Anytime,
                        -> (it ?: BigDecimal.ZERO) >= BigDecimal.ZERO
                        else -> true
                    }
                },
                defaultAmountValidation = { (it ?: BigDecimal.ZERO).easyEquals(BigDecimal.ZERO) }
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}