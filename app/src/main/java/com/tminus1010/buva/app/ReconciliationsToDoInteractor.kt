package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.data.*
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Domain
import com.tminus1010.buva.domain.Plan
import com.tminus1010.buva.domain.ReconciliationToDo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsToDoInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    currentDate: CurrentDate,
    reconciliationSkipInteractor: ReconciliationSkipInteractor,
    settingsRepo: SettingsRepo,
) {
    private val planReconciliationsToDo =
        combine(plansRepo.plans, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, reconciliationSkipInteractor.reconciliationSkips, settingsRepo.anchorDateOffset)
        { plans, transactionBlocks, reconciliations, reconciliationSkips, anchorDateOffset ->
            transactionBlocks
                .map { transactionBlock ->
                    Triple(
                        transactionBlock,
                        plans.find { it.localDatePeriod == transactionBlock.datePeriod!! },
                        reconciliations.find { it.localDate in transactionBlock.datePeriod!! }
                    )
                }
                .filter { (transactionBlock, plan, reconciliation) ->
                    (plan == null)
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c plan") }
                            && (reconciliation == null)
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c reconciliation") }
                            && transactionBlock.isFullyImported
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c isFullyImported") }
                            && transactionBlock.spendBlock.isFullyCategorized
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c isFullyCategorized") }
                            && (currentDate.flow.value !in transactionBlock.datePeriod!!)
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's current") }
                            && (!Domain.shouldSkip(reconciliationSkips, transactionBlock, anchorDateOffset))
//                        .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's skipped") }
                }
                .map { (transactionBlock) ->
                    ReconciliationToDo.PlanZ(
                        Plan(
                            transactionBlock.datePeriod!!,
                            transactionBlock.total,
                            CategoryAmounts(),
                        ),
                        transactionBlock,
                    )
                }
                .sortedByDescending { it.plan.localDatePeriod.startDate }
        }
            .sample(50)

    private val accountReconciliationsToDo =
        combine(accountsRepo.accountsAggregate, budgetedInteractor.budgeted)
        { accountsAggregate, budgeted ->
            val difference = accountsAggregate.total - budgeted.total
            if (difference.isZero) null else ReconciliationToDo.Accounts(difference)
        }

    val reconciliationsToDo =
        combine(planReconciliationsToDo, accountReconciliationsToDo)
        { planReconciliationsToDo, accountReconciliationsToDo ->
            listOf(accountReconciliationsToDo)
                .plus(planReconciliationsToDo)
                .filterNotNull()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val currentReconciliationToDo =
        reconciliationsToDo.map { it.firstOrNull() }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}