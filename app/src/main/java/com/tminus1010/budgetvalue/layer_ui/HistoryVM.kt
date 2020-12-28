package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.SourceArrayList
import com.tminus1010.budgetvalue.combineLatestImpatient
import com.tminus1010.budgetvalue.extensions.toDisplayStr
import com.tminus1010.budgetvalue.model_app.*
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import java.time.LocalDate

class HistoryVM(val transactionsVM: TransactionsVM, val reconcileVM: ReconcileVM, val planVM: PlanVM, val datePeriodGetter: DatePeriodGetter) : ViewModel() {
//    val reconciliations = SourceArrayList<Reconciliation>()
    val reconciliations = reconcileVM.activeReconcileCAs
        .map { SourceArrayList<Reconciliation>().apply { add(Reconciliation(LocalDate.now(), it) ) } }
        .flatMap { it.observable }
//    val plans = SourceArrayList<PlanAndActual>()

    val plans = planVM.planCAs
        .map { SourceArrayList<Plan>().apply { add(Plan(datePeriodGetter.getDatePeriod(LocalDate.now()), it)) } }
    // Plan comes from "saves", but there can only be 1 save per block
    // Reconciliations come from "saves"
    // Actuals comes from transactions
    val stateHistoryColumnDatas =
        combineLatestImpatient(reconciliations, plans, transactionsVM.transactionBlocks)
            .map { (reconciliations, plans, transactionBlocks) ->
                // # Define blocks
                val blockPeriodsUnsorted = ArrayList<LocalDatePeriod>()
                plans?.forEach { blockPeriodsUnsorted.add(it.localDatePeriod.blockingFirst()) }
                transactionBlocks?.forEach { blockPeriodsUnsorted.add(it.localDatePeriod) }
                reconciliations?.forEach { blockPeriodsUnsorted.add(datePeriodGetter.getDatePeriod(it.localDate).blockingFirst()) }
                val blockPeriods = blockPeriodsUnsorted.sortedBy { it.startDate }
                logz("blockPeriods:${blockPeriodsUnsorted}")
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryColumnData>()
                for (blockPeriod in blockPeriods) {
                    // ## Add Plan
                    if (plans != null)
                        plans.find { it.localDatePeriod.blockingFirst() == blockPeriod }
                            ?.also {
                                logz("Adding Plan..")
                                historyColumnDatas.add(HistoryColumnData(
                                    it.planCategoryAmounts,
                                    "Plan",
                                    it.localDatePeriod.blockingFirst().toDisplayStr(),
                                ))
                            }
                    // ## Add Actual
                    logz("actuals:${transactionBlocks}")
                    if (transactionBlocks != null)
                        transactionBlocks.find { it.localDatePeriod == blockPeriod }
                            ?.also {
                                logz("Adding Actual.. blockPeriod:${blockPeriod.startDate}")
                                historyColumnDatas.add(HistoryColumnData(
                                    it.categoryAmounts,
                                    "Actual",
                                    it.localDatePeriod.toDisplayStr(),
                                ))
                            }
                    // ## Add Reconciliations
                    logz("reconciliations:${reconciliations}")
                    if (reconciliations != null)
                        for (reconciliation in reconciliations.filter { it.localDate in blockPeriod }) {
                            logz("Adding Reconciliation..")
                            historyColumnDatas.add(HistoryColumnData(
                                reconciliation.categoryAmounts,
                                "Reconciliation",
                                reconciliation.localDate.toDisplayStr()
                            ))
                        }
                }
                historyColumnDatas
            }
            .doOnNext { logz("historyColumnData:${it}") }
            .toBehaviorSubject()

    // # Active Categories
    val activeCategories = stateHistoryColumnDatas
        .map { it.fold(HashSet<Category>()) { acc, v -> acc.apply { addAll(v.categoryAmounts.map{ it.key }) } } }
        .map { it.sortedBy { it.type } }
}