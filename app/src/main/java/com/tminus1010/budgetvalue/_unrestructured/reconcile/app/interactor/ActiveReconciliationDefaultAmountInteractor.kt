package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.Reconciliation
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
import kotlinx.coroutines.flow.map
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
        Observable.combineLatest(
            plansRepo.plans.asObservable2(),
            reconciliationsRepo.reconciliations.asObservable2(),
            transactionsInteractor.transactionBlocks2.asObservable2(),
            accountsRepo.accountsAggregate.map { it.total }.asObservable2(),
            activeReconciliationRepo.activeReconciliationCAs.asObservable2(),
            ::calcActiveReconciliationDefaultAmount
        )
            .replayNonError(1).nonLazy()

    /**
     * For clarification, take a look at the ManualCalculationsForTests excel sheet.
     */
    @VisibleForTesting
    fun calcActiveReconciliationDefaultAmount(plans: List<Plan>, reconciliations: List<Reconciliation>, transactionBlocks: List<TransactionBlock>, accountsTotal: BigDecimal, activeReconciliationCAs: CategoryAmounts): BigDecimal {
        val historyTotalAmounts = plans.map { it.total } + reconciliations.map { it.total } + transactionBlocks.map { it.amount }
        return activeReconciliationCAs.defaultAmount(accountsTotal - historyTotalAmounts.sum())
    }
}