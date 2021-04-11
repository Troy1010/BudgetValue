package com.tminus1010.budgetvalue.reconciliations.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationDomain2 @Inject constructor(
    plansRepo: IPlansRepo,
    reconciliationDomain: ReconciliationDomain,
    budgetedDomain: BudgetedDomain,
    transactionsDomain: TransactionsDomain,
) : ViewModel(), IActiveReconciliationDomain2 {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    override val defaultAmount: Observable<BigDecimal> =
        Rx.combineLatest(plansRepo.plans, reconciliationDomain.reconciliations, transactionsDomain.transactionBlocks, budgetedDomain.defaultAmount)
            .map { (plans, reconciliations, transactionBlocks, budgetedDefaultAmount) ->
                (plans.map { it.amount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount })
                    .fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    .let { budgetedDefaultAmount - it }
            }.toBehaviorSubject()
}