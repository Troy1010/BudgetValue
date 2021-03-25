package com.tminus1010.budgetvalue.features.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.features.accounts.AccountsVM
import com.tminus1010.budgetvalue.features.categories.CategoriesVM
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.features.transactions.TransactionsVM
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BudgetedVM(
    domain: Domain,
    transactionsVM: TransactionsVM,
    activeReconciliationVM: ActiveReconciliationVM,
    accountsVM: AccountsVM,
): ViewModel() {
    val categoryAmounts =
        Rx.combineLatest(domain.reconciliations, domain.plans, transactionsVM.transactionBlocks, activeReconciliationVM.activeReconcileCAs)
            .throttleLatest(1, TimeUnit.SECONDS)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs) ->
                (reconciliations + plans + transactionBlocks)
                    .map { it.categoryAmounts }
                    .plus(activeReconcileCAs)
                    .fold(hashMapOf<Category, BigDecimal>()) { acc, map ->
                        map.forEach { (k, v) -> acc[k] = (acc[k] ?: BigDecimal.ZERO) + v }
                        acc
                    }
                    .toMap()
            }
    val categoryAmountsObservableMap = categoryAmounts
        .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
        { it.itemObservableMap2 }
    val caTotal = categoryAmountsObservableMap.switchMap { it.values.total() }
    val defaultAmount =
        Rx.combineLatest(accountsVM.accountsTotal, caTotal)
            .map { it.first - it.second }
    val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .toBehaviorSubject()
}