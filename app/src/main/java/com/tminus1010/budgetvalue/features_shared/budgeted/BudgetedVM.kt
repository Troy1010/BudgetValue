package com.tminus1010.budgetvalue.features_shared.budgeted

import androidx.lifecycle.ViewModel
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
    categoriesVM: CategoriesVM,
): ViewModel() {
    val categoryAmounts =
        Rx.combineLatest(domain.reconciliations, domain.plans, transactionsVM.transactionBlocks, activeReconciliationVM.activeReconcileCAs, categoriesVM.userCategories)
            .scan(SourceHashMap<Category, BigDecimal>()) { acc, (reconciliations, plans, transactionBlocks, activeReconcileCAs, activeCategories) ->
                val newMap = mutableMapOf<Category, BigDecimal>()
                if (reconciliations != null)
                    reconciliations.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            newMap[category] = (newMap[category] ?: BigDecimal(0)) + amount
                        }
                    }
                if (plans != null)
                    plans.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            newMap[category] = (newMap[category] ?: BigDecimal(0)) + amount
                        }
                    }
                if (transactionBlocks != null)
                    transactionBlocks.forEach {
                        it.categoryAmounts.forEach { (category, amount) ->
                            newMap[category] = (newMap[category] ?: BigDecimal(0)) + amount
                        }
                    }
                if (activeReconcileCAs != null)
                    activeReconcileCAs.forEach { (category, amount) ->
                        newMap[category] = (newMap[category] ?: BigDecimal(0)) + amount
                    }
                if (activeCategories != null)
                    activeCategories
                        .filter { it !in newMap }
                        .associateWith { BigDecimal.ZERO }
                        .also { newMap.putAll(it) }
                acc.adjustTo(newMap)
                acc
            }
            .throttleLatest(1, TimeUnit.SECONDS)
            .toBehaviorSubject()
    val caTotal =
        categoryAmounts.value.itemObservableMap2
            .switchMap { it.values.total() }
    val defaultAmount =
        Rx.combineLatest(accountsVM.accountsTotal, caTotal)
            .map { it.first - it.second }
    val budgeted =
        Rx.combineLatest(categoryAmounts, defaultAmount)
            .map { Budgeted(it.first, it.second) }
            .toBehaviorSubject()
}