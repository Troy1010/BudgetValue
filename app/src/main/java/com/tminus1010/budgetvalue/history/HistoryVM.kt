package com.tminus1010.budgetvalue.history

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.history.models.HistoryColumnData
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue.plans.domain.PlansDomain
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain2
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    plansDomain: PlansDomain,
    reconciliationDomain: IReconciliationsRepo,
    transactionsDomain: TransactionsDomain,
    activeReconciliationDomain: ActiveReconciliationDomain,
    activeReconciliationDomain2: ActiveReconciliationDomain2,
    budgetedDomain: BudgetedDomain,
    private val datePeriodGetter: DatePeriodGetter
) : ViewModel() {
    val historyColumnDatas =
        Rx.combineLatest(reconciliationDomain.reconciliations, plansDomain.plans, activeReconciliationDomain2.defaultAmount, activeReconciliationDomain.activeReconcileCAs, transactionsDomain.transactionBlocks, budgetedDomain.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, activeReconciliationDefaultAmount, activeReconciliationCAs, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!datePeriodGetter.isDatePeriodValid(it.datePeriod)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod) }
                reconciliations?.forEach { blockPeriods.add(datePeriodGetter.getDatePeriod(it.localDate)) }
                plans?.forEach { blockPeriods.add(it.localDatePeriod.blockingFirst()) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<IHistoryColumnData>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks?.filter { it.datePeriod == blockPeriod }, // TODO("sort by sortDate")
                        reconciliations?.filter { it.localDate in blockPeriod },
                        plans?.filter { it.localDatePeriod.blockingFirst().startDate in blockPeriod }
                    ).flatten().also { historyColumnDatas.addAll(it) }
                }
                // ## Add Active Reconciliation
                if (activeReconciliationCAs != null && activeReconciliationDefaultAmount != null) {
                    historyColumnDatas.add(
                        HistoryColumnData(
                            "Reconciliation",
                            "Current",
                            activeReconciliationDefaultAmount,
                            activeReconciliationCAs,
                        )
                    )
                }
                // ## Add Budgeted
                if (budgeted != null) historyColumnDatas.add(budgeted)
                historyColumnDatas
            }
            .toBehaviorSubject()

    // # Active Categories
    val activeCategories = historyColumnDatas
        .map { it.fold(HashSet<Category>()) { acc, v -> acc.apply { addAll(v.categoryAmounts.map{ it.key }) } } }
        .map { it.sortedWith(categoryComparator) }
}