package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedDomain @Inject constructor(
    plansRepo: PlansRepo,
    transactionsAppService: TransactionsAppService,
    reconciliationsRepo: ReconciliationsRepo,
    accountsRepo: AccountsRepo,
) {
    val categoryAmounts =
        Rx.combineLatest(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsAppService.transactionBlocks, reconciliationsRepo.activeReconciliationCAs,)
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
        Observable.combineLatest(accountsRepo.accounts, categoryAmountsObservableMap.switchMap { it.values.total() })
        { accounts, categoryAmountsTotal ->
            accounts.total - categoryAmountsTotal
        }
            .replayNonError(1)
    val budgeted =
        Observable.combineLatest(categoryAmounts, defaultAmount, ::Budgeted)
            .replayNonError(1)
}