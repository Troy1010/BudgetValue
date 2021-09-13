package com.tminus1010.budgetvalue.history

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.budgeted.BudgetedDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.history.models.HistoryColumnData
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    plansRepo: PlansRepo,
    transactionsDomain: TransactionsDomain,
    reconciliationRepo: ReconciliationsRepo,
    activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
    budgetedDomain: BudgetedDomain,
    private val datePeriodGetter: DatePeriodGetter
) : ViewModel() {
    val historyVMItems =


        Rx.combineLatest(reconciliationRepo.reconciliations, plansRepo.plans, activeReconciliationDefaultAmountUC(), reconciliationRepo.activeReconciliationCAs, transactionsDomain.transactionBlocks, budgetedDomain.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, activeReconciliationDefaultAmount, activeReconciliationCAs, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!datePeriodGetter.isDatePeriodValid(it.datePeriod)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod) }
                reconciliations?.forEach { blockPeriods.add(datePeriodGetter.getDatePeriod(it.localDate)) }
                plans?.forEach { blockPeriods.add(it.localDatePeriod) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<IHistoryColumnData>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks?.filter { it.datePeriod == blockPeriod }, // TODO("sort by sortDate")
                        reconciliations?.filter { it.localDate in blockPeriod },
                        plans?.filter { it.localDatePeriod.startDate in blockPeriod }
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
    val activeCategories: Observable<List<Category>> =
        historyVMItems
            .map { it.fold(HashSet<Category>()) { acc, v -> acc.apply { addAll(v.categoryAmounts.map { it.key }) } } }
            .map { it.sortedWith(categoryComparator) }
}