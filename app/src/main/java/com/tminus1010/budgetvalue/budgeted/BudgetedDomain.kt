package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedDomain @Inject constructor(
    plansRepo: PlansRepo,
    transactionsDomain: TransactionsDomain,
    reconciliationsRepo: ReconciliationsRepo,
    accountsDomain: AccountsDomain,
) {
    val categoryAmounts =
        Rx.combineLatest(
            reconciliationsRepo.reconciliations,
            plansRepo.plans,
            transactionsDomain.transactionBlocks,
            reconciliationsRepo.activeReconciliationCAs,
        )
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs) ->
                (reconciliations + plans + transactionBlocks)
                    .map { it.categoryAmounts }
                    .plus(activeReconcileCAs)
                    .fold(CategoryAmounts()) { acc, map -> acc.addTogether(map) }
            }!!
    val categoryAmountsObservableMap =
        categoryAmounts
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO)) { it.itemObservableMap }
    val defaultAmount =
        Rx.combineLatest(
            accountsDomain.accountsTotal,
            categoryAmountsObservableMap.switchMap { it.values.total() },
        )
            .map { (accountsTotal, categoryAmountsTotal) ->
                accountsTotal - categoryAmountsTotal
            }
            .replay(1).refCount()!!
    val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .replay(1).refCount()!!
}