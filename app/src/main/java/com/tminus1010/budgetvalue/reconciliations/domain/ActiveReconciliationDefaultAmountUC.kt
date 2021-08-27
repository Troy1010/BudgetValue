package com.tminus1010.budgetvalue.reconciliations.domain

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
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
    transactionsDomain: TransactionsDomain,
    accountsDomain: AccountsDomain,
) {
    private val defaultAmount =
        Rx.combineLatest(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsDomain.transactionBlocks,
            accountsDomain.accountsTotal,
            reconciliationsRepo.activeReconciliationCAs,
        )
            .map { (plans, reconciliations, transactionBlocks, accountsTotal, activeReconciliationCAs) ->
                calcActiveReconciliationDefaultAmount(accountsTotal, (plans + reconciliations + transactionBlocks).map { it.totalAmount() }, activeReconciliationCAs)
            }
            .replayNonError(1).nonLazy()

    operator fun invoke(): Observable<BigDecimal> = defaultAmount

    companion object {
        /**
         * Take a look at ManualCalculationsForTests for clarification about this.
         */
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun calcActiveReconciliationDefaultAmount(accountsTotal: BigDecimal, historyTotalAmounts: Iterable<BigDecimal>, activeReconciliationCAs: CategoryAmounts): BigDecimal {
            return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
        }
    }
}