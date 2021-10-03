package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue._core.all.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.app.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    accountsRepo: AccountsRepo,
) {
    val categoryAmounts =
        Rx.combineLatest(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks, reconciliationsRepo.activeReconciliationCAs)
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs) ->
                sequenceOf<Map<Category, BigDecimal>>()
                    .plus(reconciliations.map { it.categoryAmounts })
                    .plus(plans.map { it.categoryAmounts })
                    .plus(transactionBlocks.map { it.categoryAmounts })
                    .plus(activeReconcileCAs)
                    .fold(CategoryAmounts()) { acc, map -> acc.addTogether(map) }
            }!!
            .replayNonError(1)
    val categoryAmountsObservableMap =
        categoryAmounts
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO)) { it.itemObservableMap }
    val defaultAmount =
        Observable.combineLatest(accountsRepo.accountsAggregate, categoryAmountsObservableMap.switchMap { it.values.total() })
        { accounts, categoryAmountsTotal ->
            accounts.total - categoryAmountsTotal
        }
            .replayNonError(1)
    val budgeted =
        Observable.combineLatest(categoryAmounts, defaultAmount, ::Budgeted)
            .replayNonError(1)
}