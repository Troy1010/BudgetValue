package com.tminus1010.budgetvalue.history

import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._core.repo.CurrentDatePeriod
import com.tminus1010.budgetvalue.budgeted.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.all.domain.TransactionBlock
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
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

    class PlanVMItem(plan: Plan, currentDatePeriod: CurrentDatePeriod, plansRepo: PlansRepo) : HistoryVMItem() {
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
            plan.categoryAmounts

        override val defaultAmount: String =
            plan.defaultAmount
                .toString()

        override val menuVMItems =
            listOf(
                MenuVMItem("Delete") { plansRepo.delete(plan).subscribe() }
            )
    }

    class ReconciliationVMItem(reconciliation: Reconciliation, reconciliationsRepo: ReconciliationsRepo) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            reconciliation.localDate.toDisplayStr()
                .let { Observable.just(Box(it)) }
        override val categoryAmounts =
            reconciliation.categoryAmounts
        override val defaultAmount: String =
            reconciliation.defaultAmount
                .toString()
        override val menuVMItems =
            listOf(
                MenuVMItem("Delete") { reconciliationsRepo.delete(reconciliation) }
            )
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
            transactionBlock.categoryAmounts
        override val defaultAmount: String =
            transactionBlock.defaultAmount
                .toString()
    }

    class BudgetedVMItem(budgeted: Budgeted) : HistoryVMItem() {
        override val title: String = "Budgeted"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box(null))
        override val categoryAmounts =
            budgeted.categoryAmounts
        override val defaultAmount: String =
            budgeted.defaultAmount
                .toString()
    }

    class ActiveReconciliationVMItem(override val categoryAmounts: CategoryAmounts, defaultAmount: BigDecimal) : HistoryVMItem() {
        override val title: String = "Reconciliation"
        override val subTitle: Observable<Box<String?>> =
            Observable.just(Box("Current"))
        override val defaultAmount: String =
            defaultAmount
                .toString()
    }
}