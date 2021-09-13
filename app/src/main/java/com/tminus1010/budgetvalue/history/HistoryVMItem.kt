package com.tminus1010.budgetvalue.history

import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._core.repo.CurrentDatePeriod
import com.tminus1010.budgetvalue.budgeted.Budgeted
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.transactions.models.TransactionBlock
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

sealed class HistoryVMItem {
    abstract val title: String
    abstract val subTitle: Observable<Box<String?>>
    abstract val defaultAmount: String
    abstract val categoryAmounts: List<String>

    class PlanVMItem(plan: Plan, currentDatePeriod: CurrentDatePeriod) : HistoryVMItem() {
        override val title: String = "Plan"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriod()
                .mapBox {
                    if (it == plan.localDatePeriod)
                        "Current"
                    else
                        plan.localDatePeriod.startDate.toDisplayStr()
                }
        override val categoryAmounts =
            plan.categoryAmounts.values
                .map { it.toString() }

        override val defaultAmount: String =
            plan.defaultAmount
                .toString()
    }

    class ReconciliationVMItem(reconciliation: Reconciliation) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            reconciliation.localDate.toDisplayStr()
                .let { Observable.just(Box(it)) }
        override val categoryAmounts =
            reconciliation.categoryAmounts.values
                .map { it.toString() }
        override val defaultAmount: String =
            reconciliation.defaultAmount
                .toString()
    }

    class TransactionBlockVMItem(transactionBlock: TransactionBlock, currentDatePeriod: CurrentDatePeriod) : HistoryVMItem() {
        override val title: String = "Actual"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriod()
                .mapBox {
                    if (it == transactionBlock.datePeriod)
                        "Current"
                    else
                        transactionBlock.datePeriod.startDate.toDisplayStr()
                }
        override val categoryAmounts =
            transactionBlock.categoryAmounts.values
                .map { it.toString() }
        override val defaultAmount: String =
            transactionBlock.defaultAmount
                .toString()
    }

    class BudgetedVMItem(budgeted: Budgeted) : HistoryVMItem() {
        override val title: String = "Budgeted"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box(null))
        override val categoryAmounts =
            budgeted.categoryAmounts.values
                .map { it.toString() }
        override val defaultAmount: String =
            budgeted.defaultAmount
                .toString()
    }

    class ActiveReconciliationVMItem(
        categoryAmounts: CategoryAmounts,
        defaultAmount: BigDecimal,
    ) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box("Current"))
        override val categoryAmounts =
            categoryAmounts.values
                .map { it.toString() }
        override val defaultAmount: String =
            defaultAmount
                .toString()
    }
}