package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.Reconciliation
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.plan.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActiveReconciliationDefaultAmountInteractor @Inject constructor(
    plansRepo: PlansRepo,
    reconciliationsRepo: ReconciliationsRepo,
    activeReconciliationRepo: ActiveReconciliationRepo,
    transactionsInteractor: TransactionsInteractor,
    accountsRepo: AccountsRepo,
) {
    val activeReconciliationDefaultAmount =
        combine(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsInteractor.transactionBlocks2,
            accountsRepo.accountsAggregate.map { it.total },
            activeReconciliationRepo.activeReconciliationCAs,
            ::calcActiveReconciliationDefaultAmount
        )
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    /**
     * For clarification, take a look at the ManualCalculationsForTests excel sheet.
     */
    @VisibleForTesting
    fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
        val historyTotalAmounts = plans.map { it.total } + reconciliations.map { it.total } + transactionBlocks.map { it.amount }
        return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
    }
}