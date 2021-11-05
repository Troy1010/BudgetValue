package com.tminus1010.budgetvalue.reconcile.app.interactor

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._core.all.extensions.asObservable2
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.app.AccountsAggregate
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.domain.Reconciliation
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActiveReconciliationDefaultAmountInteractor @Inject constructor(
    plansRepo: PlansRepo,
    reconciliationsRepo: ReconciliationsRepo,
    transactionsInteractor: TransactionsInteractor,
    accountsRepo: AccountsRepo,
) {
    val activeReconciliationDefaultAmount =
        Observable.combineLatest(
            plansRepo.plans.asObservable2(),
            reconciliationsRepo.reconciliations,
            transactionsInteractor.transactionBlocks,
            accountsRepo.accountsAggregate.map(AccountsAggregate::total),
            reconciliationsRepo.activeReconciliationCAs,
            ::calcActiveReconciliationDefaultAmount
        )
            .replayNonError(1).nonLazy()

    /**
     * For clarification, take a look at the ManualCalculationsForTests excel sheet.
     */
    @VisibleForTesting
    fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
        val historyTotalAmounts = plans.map { it.amount } + reconciliations.map { it.total } + transactionBlocks.map { it.amount }
        return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
    }
}