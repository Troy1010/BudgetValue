package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceArrayList
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.combineLatestImpatient
import com.example.budgetvalue.model_app.HistoryColumnData
import com.example.budgetvalue.model_app.Plan
import com.example.budgetvalue.model_app.Reconciliation
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import java.time.LocalDate

class HistoryVM(val transactionsVM: TransactionsVM, val reconcileVM: ReconcileVM, val planVM: PlanVM, val datePeriodGetter: DatePeriodGetter) : ViewModel() {
//    val reconciliations = SourceArrayList<Reconciliation>()
    val reconciliations = reconcileVM.reconcileCategoryAmounts
        .map { SourceArrayList<Reconciliation>().apply { add(Reconciliation(LocalDate.now(), it) ) } }
        .flatMap { it.observable }
//    val plans = SourceArrayList<PlanAndActual>()

    val plans = planVM.statePlanCAs
        .map { SourceArrayList<Plan>().apply { add(Plan(datePeriodGetter.getDatePeriod(LocalDate.now()), it)) } }
    // Plan comes from "saves", but there can only be 1 save per block
    // Reconciliations come from "saves"
    // Actuals comes from transactions
    val stateHistoryColumnData =
        combineLatestImpatient(reconciliations, plans, transactionsVM.transactionBlocks)
            .map { (reconciliations, plans, transactionBlocks) ->
                plans!!
                val historyColumnDatas = arrayListOf<HistoryColumnData>()
                for (plan in plans) { // TODO("Sort by startDate")
                    // # Add Plan
                    historyColumnDatas.add(HistoryColumnData(
                        "Plan",
                        plan.planCategoryAmounts
                    ))
                    // # Add Actual
                    if (transactionBlocks != null)
                        transactionBlocks.find { it.localDatePeriod == plan.localDatePeriod.blockingFirst() }
                            ?.also {
                                historyColumnDatas.add(HistoryColumnData(
                                    "Actual",
                                    it.categoryAmounts
                                ))
                            }
                    // # Add Reconciliations
                    if (reconciliations != null)
                        for (reconciliation in reconciliations.filter { it.localDate in plan.localDatePeriod.blockingFirst() }) {
                            historyColumnDatas.add(HistoryColumnData(
                                "Reconciliation",
                                reconciliation.categoryAmounts
                            ))
                        }
                }
                historyColumnDatas
            }
            .doOnNext { logz("historyColumnData:${it}") }
            .toBehaviorSubject()
}