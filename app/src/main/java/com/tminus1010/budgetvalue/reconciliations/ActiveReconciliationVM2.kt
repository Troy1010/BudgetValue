package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue._shared.domain.Domain
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate

// Separate from ActiveReconciliationVM to avoid circular dependency graph
class ActiveReconciliationVM2(
    activeReconciliationVM: ActiveReconciliationVM,
    budgetedVM: BudgetedVM,
    domain: Domain,
    transactionsVM: TransactionsVM,
) : ViewModel() {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    val defaultAmount: Observable<BigDecimal> =
        Rx.combineLatest(domain.plans, domain.reconciliations, transactionsVM.transactionBlocks, budgetedVM.defaultAmount)
            .map { (plans, reconciliations, transactionBlocks, budgetedDefaultAmount) ->
                (plans.map { it.amount } +
                        reconciliations.map { it.defaultAmount } +
                        transactionBlocks.map { it.defaultAmount })
                    .fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    .let { budgetedDefaultAmount - it }
            }.toBehaviorSubject()
    val intentSaveReconciliation: PublishSubject<Unit> = PublishSubject.create<Unit>()
        .also {
            it
                .observeOn(Schedulers.io())
                .flatMap { defaultAmount.take(1) }
                .map { defaultAmount ->
                    Reconciliation(
                        LocalDate.now(),
                        defaultAmount,
                        activeReconciliationVM.activeReconcileCAs.value.filter { it.value != BigDecimal(0) },)
                }
                .launch { domain.pushReconciliation(it).andThen(domain.clearActiveReconcileCAs()) }
        }
}