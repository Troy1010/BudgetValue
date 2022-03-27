package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.app.BudgetedInteractor
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.domain.plan.Plan
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.doLogx
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate
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
        Observable.combineLatest(plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { plans, transactionBlocks, reconciliations ->
            transactionBlocks
                .map { transactionBlock ->
                    Triple(
                        transactionBlock,
                        plans.find { it.localDatePeriod == transactionBlock.datePeriod!! },
                        reconciliations.find { it.localDate in transactionBlock.datePeriod!! }
                    )
                }
                .logx("aaa")
                .filter { (transactionBlock, plan, reconciliation) ->
                    plan == null
                            && reconciliation == null
                            && transactionBlock.isFullyImported
                            && transactionBlock.isFullyCategorized
                }
                .logx("bbb")
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
                        it.first
                    )
                }
        }
            .throttleLast(50, TimeUnit.MILLISECONDS)
            .doLogx("www")

    private val accountReconciliationsToDo =
        Observable.combineLatest(accountsRepo.accountsAggregate.asObservable2(), budgetedInteractor.budgeted)
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