package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

// Separate from ActiveReconciliationVM to avoid circular dependency graph
@HiltViewModel
class ActiveReconciliationVM2 @Inject constructor(
    activeReconciliationVM: ActiveReconciliationVM,
    budgetedVM: BudgetedVM,
    domainFacade: DomainFacade,
    transactionsVM: TransactionsVM,
) : ViewModel() {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    val defaultAmount: Observable<BigDecimal> =
        Rx.combineLatest(domainFacade.plans, domainFacade.reconciliations, transactionsVM.transactionBlocks, budgetedVM.defaultAmount)
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
                .launch { domainFacade.pushReconciliation(it).andThen(domainFacade.clearActiveReconcileCAs()) }
        }
}