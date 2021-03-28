package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.accounts.AccountsDomain
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.transactions.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import dagger.Reusable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
class BudgetedDomain @Inject constructor(
    domainFacade: DomainFacade,
    transactionsDomain: TransactionsDomain,
    activeReconciliationDomain: ActiveReconciliationDomain,
    accountsDomain: AccountsDomain
) : IBudgetedDomain {
    override val categoryAmounts =
        Rx.combineLatest(domainFacade.reconciliations, domainFacade.plans, transactionsDomain.transactionBlocks, activeReconciliationDomain.activeReconcileCAs)
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
    override val caTotal = categoryAmountsObservableMap.switchMap { it.values.total() }
    override val defaultAmount =
        Rx.combineLatest(accountsDomain.accountsTotal, caTotal)
            .map { it.first - it.second }
    override val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .toBehaviorSubject()
}