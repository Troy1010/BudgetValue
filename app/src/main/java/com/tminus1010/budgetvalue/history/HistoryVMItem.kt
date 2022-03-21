package com.tminus1010.budgetvalue.history

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue.all_features.data.repo.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.all_features.app.model.Budgeted
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.all_features.data.repo.PlansRepo
import com.tminus1010.budgetvalue.all_features.domain.plan.Plan
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.domain.Reconciliation
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * Represents a column of data in HistoryFrag
 */
sealed class HistoryVMItem {
    abstract val title: String
    abstract val subTitle: Observable<Box<String?>>
    abstract val defaultAmount: String
    protected abstract val categoryAmounts: Map<Category, BigDecimal>
    fun amountStrings(activeCategories: List<Category>) =
        activeCategories
            .map { categoryAmounts[it]?.toString() }

    open val menuVMItems: List<MenuVMItem> = listOf()

    class PlanVMItem(plan: Plan, currentDatePeriodRepo: CurrentDatePeriodRepo, plansRepo: PlansRepo) : HistoryVMItem() {
        override val title: String = "Plan"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriodRepo.currentDatePeriod
                .mapBox {
                    if (it == plan.localDatePeriod)
                        "Current"
                    else
                        plan.localDatePeriod.startDate.toDisplayStr()
                }
        override val categoryAmounts =
            plan.categoryAmounts
        override val defaultAmount =
            plan.defaultAmount.toString()
        override val menuVMItems =
            listOf(
                MenuVMItem("Delete") { GlobalScope.launch { plansRepo.delete(plan) } }
            )
    }

    class ReconciliationVMItem(reconciliation: Reconciliation, reconciliationsRepo: ReconciliationsRepo) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            reconciliation.localDate.toDisplayStr()
                .let { Observable.just(Box(it)) }
        override val categoryAmounts =
            reconciliation.categoryAmounts
        override val defaultAmount =
            reconciliation.defaultAmount.toString()
        override val menuVMItems =
            listOf(
                MenuVMItem("Delete") { reconciliationsRepo.delete(reconciliation).subscribe() }
            )
    }

    class TransactionBlockVMItem(transactionBlock: TransactionBlock, currentDatePeriodRepo: CurrentDatePeriodRepo) : HistoryVMItem() {
        override val title: String = "Actual"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriodRepo.currentDatePeriod
                .mapBox {
                    if (it == transactionBlock.datePeriod)
                        "Current"
                    else
                        transactionBlock.datePeriod!!.startDate.toDisplayStr()
                }
        override val categoryAmounts =
            transactionBlock.categoryAmounts
        override val defaultAmount =
            transactionBlock.defaultAmount.toString()
    }

    class BudgetedVMItem(budgeted: Budgeted) : HistoryVMItem() {
        override val title: String = "Budgeted"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box(budgeted.totalAmount.toString()))
        override val categoryAmounts =
            budgeted.categoryAmounts
        override val defaultAmount =
            budgeted.defaultAmount.toString()
    }

    class ActiveReconciliationVMItem(override val categoryAmounts: CategoryAmounts, defaultAmount: BigDecimal) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box("Current"))
        override val defaultAmount =
            defaultAmount.toString()
    }
}