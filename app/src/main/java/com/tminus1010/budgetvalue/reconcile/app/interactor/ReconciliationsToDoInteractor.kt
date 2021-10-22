package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.doLogx
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO()
class ReconciliationsToDoInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
) {
    private val planReconciliationsToDo =
        Observable.combineLatest(plansRepo.plans, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { plans, transactionBlocks, reconciliations ->
            transactionBlocks
                .map { transactionBlock ->
                    Triple(
                        transactionBlock,
                        plans.find { it.localDatePeriod == transactionBlock.datePeriod!! },
                        reconciliations.find { it.localDate in transactionBlock.datePeriod!! }
                    )
                }
                .filter { (transactionBlock, plan, reconciliation) ->
                    plan != null
                            && transactionBlock.isFullyImported
                            && transactionBlock.isFullyCategorized
                            && reconciliation == null
                }
                .map { ReconciliationToDo.PlanZ(it.second!!, it.first) }
        }
            .throttleLast(50, TimeUnit.MILLISECONDS)
            .doLogx("aaa")

    private val accountReconciliationsToDo =
        Observable.combineLatest(accountsRepo.accountsAggregate, budgetedInteractor.budgeted)
        { accountsAggregate, budgeted ->
            val difference = accountsAggregate.total - budgeted.totalAmount
            Box(if (difference.isZero) null else ReconciliationToDo.Accounts(difference))
        }
            .replay(1).refCount()

    val reconciliationsToDo =
        Observable.combineLatest(planReconciliationsToDo, accountReconciliationsToDo)
        { planReconciliationsToDo, (accountReconciliationsToDo) ->
            listOf(
                listOf(accountReconciliationsToDo),
                planReconciliationsToDo,
            ).flatten().filterNotNull()
        }
            .replay(1).refCount()
}