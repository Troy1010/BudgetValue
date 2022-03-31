package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.Budgeted
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.framework.observable.Rx
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
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
        Rx.combineLatest(reconciliationsRepo.reconciliations.asObservable2(), plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks.asObservable2())
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks) ->
                sequenceOf<Map<Category, BigDecimal>>()
                    .plus(reconciliations.map { it.categoryAmounts })
                    .plus(plans.map { it.categoryAmounts })
                    .plus(transactionBlocks.map { it.categoryAmounts })
                    .fold(CategoryAmounts()) { acc, map -> acc.addTogether(map) }
            }
            .replayNonError(1)
    val totalAmount =
        Observable.combineLatest(reconciliationsRepo.reconciliations.asObservable2(), plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks.asObservable2())
        { reconciliations, plans, actuals ->
            reconciliations.map { it.total }.sum() + // TODO: Duplication of ActiveReconciliationInteractor
                    plans.map { it.total }.sum() +
                    actuals.map { it.total }.sum()
        }
            .throttleLast(50, TimeUnit.MILLISECONDS)
            .replayNonError(1)
    val budgeted =
        Observable.combineLatest(categoryAmounts, totalAmount, ::Budgeted)
            .replayNonError(1)
    val difference =
        Observable.combineLatest(accountsRepo.accountsAggregate.asObservable2(), budgeted)
        { accountsAggregate, budgeted ->
            accountsAggregate.total - budgeted.categoryAmounts.values.sum()
        }
            .replay(1).refCount()
}