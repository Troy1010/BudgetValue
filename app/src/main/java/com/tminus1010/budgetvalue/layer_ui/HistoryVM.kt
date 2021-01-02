package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.combineLatestImpatient
import com.tminus1010.budgetvalue.extensions.toDisplayStr
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.HistoryColumnData
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.source_objects.SourceArrayList
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class HistoryVM(
    private val repo: Repo,
    val transactionsVM: TransactionsVM,
    val activeReconciliationVM: ActiveReconciliationVM,
    val planVM: PlanVM,
    val datePeriodGetter: DatePeriodGetter,
) : ViewModel() {

//    val reconciliations = SourceArrayList<Reconciliation>()
//    val reconciliations = activeReconciliationVM.activeReconcileCAs
//        .map { SourceArrayList<Reconciliation>().apply { add(Reconciliation(LocalDate.now(), it) ) } }
//        .flatMap { it.observable }
//    val plans = SourceArrayList<PlanAndActual>()

    val plans = combineLatestAsTuple(planVM.defaultAmount, planVM.planCAs)
        .map { SourceArrayList<Plan>().apply { add(Plan(datePeriodGetter.getDatePeriod(LocalDate.now()), it.first, it.second)) } }
    // Plan comes from "saves", but there can only be 1 save per block
    // Reconciliations come from "saves"
    // Actuals comes from transactions
    val historyColumnDatas =
        combineLatestImpatient(repo.fetchReconciliations(), activeReconciliationVM.activeReconcileCAs, activeReconciliationVM.defaultAmount, plans, transactionsVM.transactionBlocks)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .throttleLast(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, activeReconciliationCAs, activeReconciliationDefaultAmount, plans, transactionBlocks) ->
                logz("!*!*! start historyColumnDatas")
                // # Define blocks
                val blockPeriodsUnsorted = mutableSetOf<LocalDatePeriod>()
                plans?.forEach { blockPeriodsUnsorted.add(it.localDatePeriod.blockingFirst()) }
                transactionBlocks?.forEach { blockPeriodsUnsorted.add(it.localDatePeriod) }
                reconciliations?.forEach { blockPeriodsUnsorted.add(datePeriodGetter.getDatePeriod(it.localDate).blockingFirst()) }
                val blockPeriods = blockPeriodsUnsorted.sortedBy { it.startDate }
                logz("zzz blockPeriodsUnsorted:${blockPeriodsUnsorted}")
                logz("zzz blockPeriods:${blockPeriods}")
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryColumnData>()
                for (blockPeriod in blockPeriods) {
                    logz("blockPeriod:${blockPeriod}")
                    // ## Add Plan
                    if (plans != null)
                        plans.find { it.localDatePeriod.blockingFirst() == blockPeriod }
                            ?.also {
                                logz("Adding Plan..")
                                historyColumnDatas.add(HistoryColumnData(
                                    "Plan",
                                    it.localDatePeriod.blockingFirst().toDisplayStr(),
                                    it.defaultAmount,
                                    it.categoryAmounts,
                                ))
                            }
                    // ## Add Actual
                    logz("actuals:${transactionBlocks}")
                    if (transactionBlocks != null)
                        transactionBlocks.find { it.localDatePeriod == blockPeriod }
                            ?.also {
                                logz("Adding Actual.. blockPeriod:${blockPeriod.startDate}")
                                historyColumnDatas.add(HistoryColumnData(
                                    "Actual",
                                    it.localDatePeriod.toDisplayStr(),
                                    it.defaultAmount,
                                    it.categoryAmounts,
                                ))
                            }
                    // ## Add Reconciliations
                    logz("reconciliations:${reconciliations}")
                    if (reconciliations != null)
                        for (reconciliation in reconciliations.filter { it.localDate in blockPeriod }) {
                            logz("Adding Reconciliation.. TTT:${reconciliation.categoryAmounts.entries.first()}")
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
                historyColumnDatas
            }
            .toBehaviorSubject()

    // # Active Categories
    val activeCategories = historyColumnDatas
        .map { it.fold(HashSet<Category>()) { acc, v -> acc.apply { addAll(v.categoryAmounts.map{ it.key }) } } }
        .map { it.sortedBy { it.type } }
}