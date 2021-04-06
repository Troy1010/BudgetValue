package com.tminus1010.budgetvalue.reconciliations.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue.plans.domain.PlansDomain
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationDomain2 @Inject constructor(
    plansDomain: PlansDomain,
    reconciliationDomain: ReconciliationDomain,
    activeReconciliationDomain: ActiveReconciliationDomain,
    budgetedDomain: BudgetedDomain,
    transactionsDomain: TransactionsDomain,
) : ViewModel(), IActiveReconciliationDomain2 {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    override val defaultAmount: Observable<BigDecimal> =
        Rx.combineLatest(plansDomain.plans, reconciliationDomain.reconciliations, transactionsDomain.transactionBlocks, budgetedDomain.defaultAmount)
            .map { (plans, reconciliations, transactionBlocks, budgetedDefaultAmount) ->
                (plans.map { it.amount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount })
                    .fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    .let { budgetedDefaultAmount - it }
            }.toBehaviorSubject()
    override val intentSaveReconciliation: PublishSubject<Unit> = PublishSubject.create<Unit>()
        .also {
            it
                .observeOn(Schedulers.io())
                .flatMap { defaultAmount.take(1) }
                .map { defaultAmount ->
                    Reconciliation(
                        LocalDate.now(),
                        defaultAmount,
                        activeReconciliationDomain.activeReconcileCAs.value.filter { it.value != BigDecimal(0) },)
                }
                .launch { reconciliationDomain.pushReconciliation(it).andThen(reconciliationDomain.clearActiveReconcileCAs()) }
        }
}