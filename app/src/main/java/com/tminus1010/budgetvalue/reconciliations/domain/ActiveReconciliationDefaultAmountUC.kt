package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationDefaultAmountUC @Inject constructor(
    plansRepo: PlansRepo,
    reconciliationsRepo: IReconciliationsRepo,
    budgetedDomain: BudgetedDomain,
    transactionsDomain: TransactionsDomain,
) {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    private val defaultAmount =
        Rx.combineLatest(plansRepo.plans, reconciliationsRepo.reconciliations, transactionsDomain.transactionBlocks, budgetedDomain.defaultAmount)
            .map { (plans, reconciliations, transactionBlocks, budgetedDefaultAmount) ->
                (plans.map { it.amount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount })
                    .let { budgetedDefaultAmount - it.sum() }
            }.replay(1).also { it.connect() } // TODO: No lifecycle to give to..? Seems like a coding error. Shouldn't this be in a VM?
    operator fun invoke(): Observable<BigDecimal> = defaultAmount
}