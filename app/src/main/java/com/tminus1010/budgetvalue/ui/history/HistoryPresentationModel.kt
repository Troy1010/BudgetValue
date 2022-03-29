package com.tminus1010.budgetvalue.ui.history

import com.tminus1010.budgetvalue.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue.data.CurrentDatePeriod
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.budgetvalue.domain.Budgeted
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.domain.plan.Plan
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.Reconciliation
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * Represents a column of data in HistoryFrag
 */
sealed class HistoryPresentationModel {
    abstract val title: String
    abstract val subTitle: Observable<Box<String?>>
    abstract val defaultAmount: String
    protected abstract val categoryAmounts: Map<Category, BigDecimal>
    fun amountStrings(activeCategories: List<Category>) =
        activeCategories
            .map { categoryAmounts[it]?.toString() }

    open val menuVMItems: List<MenuVMItem> = listOf()

    class PlanPresentationModel(plan: Plan, currentDatePeriod: CurrentDatePeriod, plansRepo: PlansRepo) : HistoryPresentationModel() {
        override val title: String = "Plan"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriod.currentDatePeriod
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

    class ReconciliationPresentationModel(reconciliation: Reconciliation, reconciliationsRepo: ReconciliationsRepo) : HistoryPresentationModel() {
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
                MenuVMItem(
                    title = "Delete",
                    onClick = { GlobalScope.launch { reconciliationsRepo.delete(reconciliation) } }
                )
            )
    }

    class TransactionBlockPresentationModel(transactionBlock: TransactionBlock, currentDatePeriod: CurrentDatePeriod) : HistoryPresentationModel() {
        override val title: String = "Actual"
        override val subTitle: Observable<Box<String?>> =
            currentDatePeriod.currentDatePeriod
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

    class BudgetedPresentationModel(budgeted: Budgeted) : HistoryPresentationModel() {
        override val title: String = "Budgeted"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box(budgeted.totalAmount.toString()))
        override val categoryAmounts =
            budgeted.categoryAmounts
        override val defaultAmount =
            budgeted.defaultAmount.toString()
    }

    class ActiveReconciliationPresentationModel(override val categoryAmounts: CategoryAmounts, defaultAmount: BigDecimal) : HistoryPresentationModel() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box("Current"))
        override val defaultAmount =
            defaultAmount.toString()
    }
}