package com.tminus1010.budgetvalue.reconcile.app

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.all.domain.models.AccountList
import com.tminus1010.budgetvalue.all.domain.models.TransactionBlock
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActiveReconciliationDefaultAmountUC @Inject constructor(
    plansRepo: PlansRepo,
    reconciliationsRepo: ReconciliationsRepo,
    transactionsAppService: TransactionsAppService,
    accountsRepo: AccountsRepo,
) {
    private val defaultAmount =
        Observable.combineLatest(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsAppService.transactionBlocks,
            accountsRepo.accounts.map(AccountList::total),
            reconciliationsRepo.activeReconciliationCAs,
            Companion::calcActiveReconciliationDefaultAmount
        )
            .replayNonError(1).nonLazy()

    operator fun invoke(): Observable<BigDecimal> = defaultAmount

    companion object {
        /**
         * For clarification, take a look at the ManualCalculationsForTests excel sheet.
         */
        @VisibleForTesting
        fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
            val historyTotalAmounts = plans.map { it.amount } + reconciliations.map { it.totalAmount } + transactionBlocks.map { it.amount }
            return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
        }
    }
}