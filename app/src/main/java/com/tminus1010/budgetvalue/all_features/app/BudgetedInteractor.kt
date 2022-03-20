package com.tminus1010.budgetvalue.all_features.app

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.all_features.framework.Rx
import com.tminus1010.budgetvalue.all_features.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.all_features.data.repo.AccountsRepo
import com.tminus1010.budgetvalue.all_features.app.model.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
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
        Rx.combineLatest(reconciliationsRepo.reconciliations, plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks)
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks) ->
                sequenceOf<Map<Category, BigDecimal>>()
                    .plus(reconciliations.map { it.categoryAmounts })
                    .plus(plans.map { it.categoryAmounts })
                    .plus(transactionBlocks.map { it.categoryAmounts })
                    .fold(CategoryAmounts()) { acc, map -> acc.addTogether(map) }
            }
            .replayNonError(1)
    val categoryAmountsObservableMap =
        categoryAmounts
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO)) { it.itemObservableMap }
    val totalAmount =
        Observable.combineLatest(reconciliationsRepo.reconciliations, plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks)
        { reconciliations, plans, actuals ->
            reconciliations.map { it.total }.sum() +
                    plans.map { it.total }.sum() +
                    actuals.map { it.amount }.sum()
        }
            .throttleLast(50, TimeUnit.MILLISECONDS)
            .replayNonError(1)

    @Deprecated("use budgeted.defaultAmount")
    val defaultAmount =
        Observable.combineLatest(totalAmount, categoryAmountsObservableMap.switchMap { it.values.total() })
        { totalAmount, caTotal -> totalAmount - caTotal }
            .replayNonError(1)
    val budgeted =
        Observable.combineLatest(categoryAmounts, totalAmount, ::Budgeted)
            .replayNonError(1)
    val difference =
        Observable.combineLatest(accountsRepo.accountsAggregate, budgeted)
        { accountsAggregate, budgeted ->
            accountsAggregate.total - budgeted.categoryAmounts.values.sum()
        }
            .replay(1).refCount()
}