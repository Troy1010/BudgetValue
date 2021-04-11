package com.tminus1010.budgetvalue.reconciliations.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationDomain2 @Inject constructor(
    plansRepo: IPlansRepo,
    reconciliationsRepo: IReconciliationsRepo,
    budgetedDomain: BudgetedDomain,
    transactionsDomain: TransactionsDomain,
) : ViewModel(), IActiveReconciliationDomain2 {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    override val defaultAmount: Observable<BigDecimal> =
        Rx.combineLatest(plansRepo.plans, reconciliationsRepo.reconciliations, transactionsDomain.transactionBlocks, budgetedDomain.defaultAmount)
            .map { (plans, reconciliations, transactionBlocks, budgetedDefaultAmount) ->
                (plans.map { it.amount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount })
                    .let { budgetedDefaultAmount - it.sum() }
            }.toBehaviorSubject()
}