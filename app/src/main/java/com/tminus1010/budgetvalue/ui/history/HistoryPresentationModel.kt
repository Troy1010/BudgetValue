package com.tminus1010.budgetvalue.ui.history

import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.data.CurrentDatePeriod
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.*
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.view.NativeText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * Represents a column of data in HistoryFrag
 */
sealed class HistoryPresentationModel {
    abstract val title: String
    abstract val subTitle: Flow<NativeText?>
    abstract val defaultAmount: String
    protected abstract val categoryAmounts: Map<Category, BigDecimal>
    fun amountStrings(activeCategories: List<Category>) =
        activeCategories
            .map { categoryAmounts[it]?.toString() }

    open val menuVMItems: List<MenuVMItem> = listOf()

    class PlanPresentationModel(plan: Plan, currentDatePeriod: CurrentDatePeriod, plansRepo: PlansRepo) : HistoryPresentationModel() {
        override val title: String = "Plan"
        override val subTitle: Flow<NativeText?> =
            currentDatePeriod.flow
                .map {
                    if (it == plan.localDatePeriod)
                        NativeText.Simple("Current")
                    else
                        NativeText.Simple(plan.localDatePeriod.startDate.toDisplayStr())
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
        override val subTitle: Flow<NativeText?> =
            flowOf(NativeText.Simple(reconciliation.localDate.toDisplayStr()))
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
        override val subTitle: Flow<NativeText?> =
            currentDatePeriod.flow
                .map {
                    if (it == transactionBlock.datePeriod)
                        NativeText.Simple("Current")
                    else
                        NativeText.Simple(transactionBlock.datePeriod!!.startDate.toDisplayStr())
                }
        override val categoryAmounts =
            transactionBlock.categoryAmounts
        override val defaultAmount =
            transactionBlock.defaultAmount.toString()
    }

    class BudgetedPresentationModel(budgeted: Budgeted) : HistoryPresentationModel() {
        override val title: String = "Budgeted"
        override val subTitle: Flow<NativeText?> =
            flowOf(NativeText.Simple(budgeted.totalAmount.toString()))
        override val categoryAmounts =
            budgeted.categoryAmounts
        override val defaultAmount =
            budgeted.defaultAmount.toString()
    }

    class ActiveReconciliationPresentationModel(override val categoryAmounts: CategoryAmounts, defaultAmount: BigDecimal) : HistoryPresentationModel() {
        override val title: String = "Reconciliation"
        override val subTitle: Flow<NativeText?> =
            flowOf(NativeText.Simple("Current"))
        override val defaultAmount =
            defaultAmount.toString()
    }
}