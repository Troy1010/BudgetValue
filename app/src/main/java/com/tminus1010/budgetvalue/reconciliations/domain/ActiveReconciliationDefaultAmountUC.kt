package com.tminus1010.budgetvalue.reconciliations.domain

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.budgetvalue.all.domain.models.TransactionBlock
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
        Observable.combineLatest(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsDomain.transactionBlocks,
            accountsDomain.accountsTotal,
            reconciliationsRepo.activeReconciliationCAs,
            ::calcActiveReconciliationDefaultAmount
        )
            .replayNonError(1).nonLazy()

    operator fun invoke(): Observable<BigDecimal> = defaultAmount

    companion object {
        /**
         * For clarification, take a look at the ManualCalculationsForTests excel sheet.
         */
        @VisibleForTesting
        fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
            val historyTotalAmounts = (plans + reconciliations + transactionBlocks).map { it.totalAmount() }
            return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
        }
    }
}