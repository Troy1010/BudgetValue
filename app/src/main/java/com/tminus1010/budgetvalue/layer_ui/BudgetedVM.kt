package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.combineLatestImpatient
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
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
        combineLatestImpatient(domain.reconciliations, domain.plans, transactionsVM.transactionBlocks, activeReconciliationVM.activeReconcileCAs, domain.activeCategories)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs, activeCategories) ->
                val x = SourceHashMap<Category, BigDecimal>()
                if (reconciliations != null) {
                    reconciliations.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            x[category] = (x[category]?:BigDecimal(0)) + amount
                        }
                    }
                }
                if (plans != null) {
                    plans.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            x[category] = (x[category]?:BigDecimal(0)) + amount
                        }
                    }
                }
                if (transactionBlocks != null) {
                    transactionBlocks.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            x[category] = (x[category]?:BigDecimal(0)) + amount
                        }
                    }
                }
                if (activeReconcileCAs != null) {
                    activeReconcileCAs.forEach { (category, amount) ->
                        x[category] = (x[category]?:BigDecimal(0)) + amount
                    }
                }
                if (activeCategories != null)
                    activeCategories
                        .filter { it in x }
                        .associateWith { BigDecimal.ZERO }
                        .also { x.putAll(it) }
                x
            }
            .throttleLatest(1, TimeUnit.SECONDS)
            .toBehaviorSubject()
    val caTotal =
        categoryAmounts.value.itemObservableMap2
            .switchMap { it.values.total() }
            .replay(1).refCount()
    val defaultAmount =
        combineLatestAsTuple(accountsVM.accountsTotal, caTotal)
            .map { it.first - it.second }
            .toBehaviorSubject()
}