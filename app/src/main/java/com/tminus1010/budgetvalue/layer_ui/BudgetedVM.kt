package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestImpatient
import com.tminus1010.budgetvalue.extensions.logzz
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BudgetedVM(
    repo: Repo,
    transactionsVM: TransactionsVM,
    activeReconciliationVM: ActiveReconciliationVM,
): ViewModel() {
    val defaultAmount =
        combineLatestImpatient(repo.fetchReconciliations(), repo.plans, transactionsVM.transactionBlocks, activeReconciliationVM.defaultAmount)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileDefaultAmount) ->
                var returning = BigDecimal(0)
                if (reconciliations != null) {
                    reconciliations.forEach {
                        returning += it.defaultAmount
                    }
                }
                if (plans != null) {
                    plans.forEach {
                        returning += it.defaultAmount
                    }
                }
                if (transactionBlocks != null) {
                    transactionBlocks.forEach {
                        returning += it.defaultAmount
                    }
                }
                if (activeReconcileDefaultAmount != null) {
                    returning += activeReconcileDefaultAmount
                }
                returning
            }
            .throttleLast(1, TimeUnit.SECONDS)
            .toBehaviorSubject()
    val categoryAmounts =
        combineLatestImpatient(repo.fetchReconciliations(), repo.plans, transactionsVM.transactionBlocks, activeReconciliationVM.activeReconcileCAs)
            .map { (reconciliations, plans, transactionBlocks, activeReconcileCAs) ->
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
                x
            }
            .throttleLast(1, TimeUnit.SECONDS)
            .toBehaviorSubject()
}