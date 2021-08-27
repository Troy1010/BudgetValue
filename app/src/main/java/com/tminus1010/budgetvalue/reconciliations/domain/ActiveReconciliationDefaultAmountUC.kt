package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationDefaultAmountUC(
    historyTotalAmounts: Observable<List<BigDecimal>>,
    accountsTotal: Observable<BigDecimal>,
    activeReconciliationCAs: Observable<CategoryAmounts>,
) {
    @Inject
    constructor(
        plansRepo: PlansRepo,
        reconciliationsRepo: ReconciliationsRepo,
        transactionsDomain: TransactionsDomain,
        accountsDomain: AccountsDomain,
    ) : this(
        Observable.merge(
            plansRepo.plans,
            reconciliationsRepo.reconciliations,
            transactionsDomain.transactionBlocks
        ).map { it.map { it.totalAmount() } },
        accountsDomain.accountsTotal,
        reconciliationsRepo.activeReconciliationCAs
            .map { CategoryAmounts(it) },
    )

    private val totalAmount =
        Observables.combineLatest(accountsTotal, historyTotalAmounts)
            .map { (accountsTotal, historyTotalAmounts) ->
                accountsTotal - historyTotalAmounts.sum()
            }

    private val defaultAmount =
        Observables.combineLatest(totalAmount, activeReconciliationCAs)
            .map { (totalAmount, activeReconciliationCAs) ->
                activeReconciliationCAs.defaultAmount(totalAmount)
            }
            .replay(1).autoConnect()

    operator fun invoke(): Observable<BigDecimal> = defaultAmount
}