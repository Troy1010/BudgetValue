package com.tminus1010.budgetvalue.budgeted.domain

import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.budgeted.models.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedDomain @Inject constructor(
    plansRepo: IPlansRepo,
    transactionsDomain: TransactionsDomain,
    reconciliationsRepo: IReconciliationsRepo,
    accountsDomain: AccountsDomain,
) : IBudgetedDomain {
    override val categoryAmounts =
        Rx.combineLatest(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsDomain.transactionBlocks, reconciliationsRepo.activeReconciliationCAs)
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
    override val defaultAmount =
        Rx.combineLatest(
            accountsDomain.accountsTotal,
            categoryAmountsObservableMap.switchMap { it.values.total() },
        ).map { it.first - it.second }
            .replay(1).refCount()
    override val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .toBehaviorSubject()
}