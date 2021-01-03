package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestImpatient
import com.tminus1010.budgetvalue.extensions.toDisplayStr
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.HistoryColumnData
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class HistoryVM(
    private val repo: Repo,
    val transactionsVM: TransactionsVM,
    val activeReconciliationVM: ActiveReconciliationVM,
    val planVM: PlanVM,
    val datePeriodGetter: DatePeriodGetter,
) : ViewModel() {
    // Plans comes from "saves", but there can only be 1 save per block
    // Reconciliations come from "saves"
    // Actuals comes from transactions
    val historyColumnDatas =
        combineLatestImpatient(repo.fetchReconciliations(), activeReconciliationVM.defaultAmount, activeReconciliationVM.activeReconcileCAs, planVM.defaultAmount, planVM.planCAs, transactionsVM.transactionBlocks)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .throttleLast(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, activeReconciliationDefaultAmount, activeReconciliationCAs, planDefaultAmount, planCAs, transactionBlocks) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!datePeriodGetter.isDatePeriodValid(it.datePeriod)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod) }
                reconciliations?.forEach { blockPeriods.add(datePeriodGetter.getDatePeriod(it.localDate)) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryColumnData>()
                for (blockPeriod in blockPeriods) {
                    // ## Add Actuals
                    if (transactionBlocks != null)
                        transactionBlocks.find { it.datePeriod == blockPeriod }
                            ?.also {
                                historyColumnDatas.add(HistoryColumnData(
                                    "Actual",
                                    it.datePeriod.toDisplayStr(),
                                    it.defaultAmount,
                                    it.categoryAmounts,
                                ))
                            }
                    // ## Add Reconciliations
                    if (reconciliations != null)
                        for (reconciliation in reconciliations.filter { it.localDate in blockPeriod }) {
                            historyColumnDatas.add(HistoryColumnData(
                                "Reconciliation",
                                reconciliation.localDate.toDisplayStr(),
                                reconciliation.defaultAmount,
                                reconciliation.categoryAmounts,
                            ))
                        }
                }
                // ## Add Active Reconciliation
                if (activeReconciliationCAs != null && activeReconciliationDefaultAmount != null) {
                    historyColumnDatas.add(HistoryColumnData(
                        "Reconciliation",
                        "Current",
                        activeReconciliationDefaultAmount,
                        activeReconciliationCAs,
                    ))
                }
                // ## Add Active Plan
                if (planCAs != null && planDefaultAmount != null) {
                    historyColumnDatas.add(HistoryColumnData(
                        "Plan",
                        "Current",
                        planDefaultAmount,
                        planCAs,
                    ))
                }
                historyColumnDatas
            }
            .toBehaviorSubject()

    // # Active Categories
    val activeCategories = historyColumnDatas
        .map { it.fold(HashSet<Category>()) { acc, v -> acc.apply { addAll(v.categoryAmounts.map{ it.key }) } } }
        .map { it.sortedBy { it.type } }
}