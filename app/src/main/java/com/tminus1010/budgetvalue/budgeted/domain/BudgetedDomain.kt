package com.tminus1010.budgetvalue.budgeted.domain

import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.budgeted.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.domain.ReconciliationDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedDomain @Inject constructor(
    reconciliationDomain: ReconciliationDomain,
    plansRepo: IPlansRepo,
    transactionsDomain: TransactionsDomain,
    activeReconciliationDomain: ActiveReconciliationDomain,
    accountsDomain: AccountsDomain,
) : IBudgetedDomain {
    override val categoryAmounts =
        Rx.combineLatest(reconciliationDomain.reconciliations, plansRepo.plans, transactionsDomain.transactionBlocks, activeReconciliationDomain.activeReconcileCAs)
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs) ->
                (reconciliations + plans + transactionBlocks)
                    .map { it.categoryAmounts }
                    .plus(activeReconcileCAs)
                    .fold(hashMapOf<Category, BigDecimal>()) { acc, map ->
                        map.forEach { (k, v) -> acc[k] = (acc[k] ?: BigDecimal.ZERO) + v }
                        acc
                    }
                    .toMap()
            }
    override val categoryAmountsObservableMap = categoryAmounts
        .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
        { it.itemObservableMap2 }
    private val caTotal = categoryAmountsObservableMap.switchMap { it.values.total() }
    override val defaultAmount =
        Rx.combineLatest(accountsDomain.accountsTotal, caTotal)
            .map { it.first - it.second }
    override val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .toBehaviorSubject()
}