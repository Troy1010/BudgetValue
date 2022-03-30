package com.tminus1010.budgetvalue.app

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.CategoryAmountsAndTotal
import com.tminus1010.budgetvalue.domain.Reconciliation
import com.tminus1010.budgetvalue.domain.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.rx3.asFlow
import java.math.BigDecimal
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationRepo: ActiveReconciliationRepo,
    plansRepo: PlansRepo,
    reconciliationsRepo: ReconciliationsRepo,
    transactionsInteractor: TransactionsInteractor,
) {
    val categoryAmountsAndTotal =
        combine(activeReconciliationRepo.activeReconciliationCAs, accountsRepo.accountsAggregate, budgetedInteractor.budgeted.asFlow())
        { activeReconciliationCAs, accountsAggregate, budgeted ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = accountsAggregate.total - budgeted.totalAmount,
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val defaultAmount =
        combine(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsInteractor.transactionBlocks,
            accountsRepo.accountsAggregate.map { it.total },
            activeReconciliationRepo.activeReconciliationCAs,
            ::calcActiveReconciliationDefaultAmount
        )
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    /**
     * For clarification, take a look at the ManualCalculationsForTests excel sheet.
     */
    @VisibleForTesting
    fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
        val historyTotalAmounts = plans.map { it.total } + reconciliations.map { it.total } + transactionBlocks.map { it.amount }
        return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
    }
}