package com.tminus1010.buva.ui.history

import com.tminus1010.buva.app.ReconciliationSkipInteractor
import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmountsAndTotal
import com.tminus1010.buva.domain.Reconciliation
import com.tminus1010.buva.domain.TransactionBlock
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
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
    abstract val accountsTotal: Flow<NativeText?>
    abstract val difference: Flow<NativeText?>
    abstract val incomeTotal: Flow<NativeText?>
    abstract val spendTotal: Flow<NativeText?>
    abstract val default: Flow<NativeText?>
    protected abstract val categoryAmounts: Map<Category, BigDecimal>
    fun amountStrings(activeCategories: List<Category>) =
        activeCategories
            .map { categoryAmounts[it]?.toString() }

    open val menuVMItems: List<MenuVMItem> = listOf()

    class ReconciliationPresentationModel(reconciliation: Reconciliation, reconciliationsRepo: ReconciliationsRepo) : HistoryPresentationModel() {
        override val accountsTotal: Flow<NativeText?> = flowOf(null)
        override val difference: Flow<NativeText?> = flowOf(NativeText.Simple(reconciliation.total.toString()))
        override val incomeTotal: Flow<NativeText?> = flowOf(null)
        override val spendTotal: Flow<NativeText?> = flowOf(null)
        override val default: Flow<NativeText?> = flowOf(NativeText.Simple(reconciliation.defaultAmount.toString()))
        override val title: String = "Reconciliation"
        override val subTitle: Flow<NativeText?> =
            flowOf(NativeText.Simple(reconciliation.date.toDisplayStr()))
        override val categoryAmounts =
            reconciliation.categoryAmounts
        override val menuVMItems =
            listOf(
                MenuVMItem(
                    title = "Delete",
                    onClick = { GlobalScope.launch { reconciliationsRepo.delete(reconciliation) } }
                )
            )
    }

    class TransactionBlockPresentationModel(transactionBlock: TransactionBlock, accountsTotalEstimate: BigDecimal?, currentDatePeriod: CurrentDatePeriod, hasSkip: Boolean, reconciliationSkipInteractor: ReconciliationSkipInteractor) : HistoryPresentationModel() {
        override val accountsTotal: Flow<NativeText?> = flowOf(NativeText.Simple(accountsTotalEstimate?.toString() ?: ""))
        override val difference: Flow<NativeText?> = flowOf(NativeText.Simple(transactionBlock.total.toString()))
        override val incomeTotal: Flow<NativeText?> = flowOf(NativeText.Simple(transactionBlock.incomeBlock.total.toString()))
        override val spendTotal: Flow<NativeText?> = flowOf(NativeText.Simple(transactionBlock.spendBlock.total.toString()))
        override val default: Flow<NativeText?> = flowOf(NativeText.Simple(transactionBlock.defaultAmount.toString()))
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
        override val menuVMItems =
            listOfNotNull(
                if (hasSkip)
                    MenuVMItem(
                        title = "Remove Skip",
                        onClick = { GlobalScope.launch { reconciliationSkipInteractor.removeSkipIn(transactionBlock) } },
                    )
                else null
            )
    }

    class BudgetedPresentationModel(categoryAmountsAndTotal: CategoryAmountsAndTotal) : HistoryPresentationModel() {
        override val accountsTotal: Flow<NativeText?> = flowOf(NativeText.Simple(categoryAmountsAndTotal.total.toString()))
        override val difference: Flow<NativeText?> = flowOf(null)
        override val incomeTotal: Flow<NativeText?> = flowOf(null)
        override val spendTotal: Flow<NativeText?> = flowOf(null)
        override val default: Flow<NativeText?> = flowOf(NativeText.Simple(categoryAmountsAndTotal.defaultAmount.toString()))
        override val title: String = "Budgeted"
        override val subTitle: Flow<NativeText?> = flowOf(null)
        override val categoryAmounts = categoryAmountsAndTotal.categoryAmounts
    }
}