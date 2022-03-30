package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.domain.Plan
import com.tminus1010.tmcommonkotlin.coroutines.extensions.doLogx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx3.asFlow
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

// TODO()
@Singleton
class ReconciliationsToDoInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
) {
    private val planReconciliationsToDo =
        combine(plansRepo.plans, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { plans, transactionBlocks, reconciliations ->
            transactionBlocks
                .map { transactionBlock ->
                    Triple(
                        transactionBlock,
                        plans.find { it.localDatePeriod == transactionBlock.datePeriod!! },
                        reconciliations.find { it.localDate in transactionBlock.datePeriod!! }
                    )
                }
                .logx("planReconciliationsToDo 111")
                .filter { (transactionBlock, plan, reconciliation) ->
                    plan == null
                            && reconciliation == null
                            && transactionBlock.isFullyImported
                            && transactionBlock.isFullyCategorized
                }
                .logx("planReconciliationsToDo 222")
                .map {
                    ReconciliationToDo.PlanZ(
                        Plan(
                            LocalDatePeriod(
                                LocalDate.of(2020, 1, 1),
                                LocalDate.of(2020, 1, 1)
                            ),
                            BigDecimal.TEN,
                            CategoryAmounts(),
                        ),
                        it.first,
                    )
                }
        }
            .sample(50)
            .doLogx("planReconciliationsToDo 333")

    private val accountReconciliationsToDo =
        combine(accountsRepo.accountsAggregate, budgetedInteractor.budgeted.asFlow())
        { accountsAggregate, budgeted ->
            val difference = accountsAggregate.total - budgeted.totalAmount
            if (difference.isZero) null else ReconciliationToDo.Accounts(difference)
        }

    val reconciliationsToDo =
        combine(planReconciliationsToDo, accountReconciliationsToDo)
        { planReconciliationsToDo, accountReconciliationsToDo ->
            listOf(
                listOf(accountReconciliationsToDo),
                planReconciliationsToDo,
            ).flatten().filterNotNull()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val currentReconciliationToDo =
        reconciliationsToDo.map { it.firstOrNull() }
}