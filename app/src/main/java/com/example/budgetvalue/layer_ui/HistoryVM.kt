package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceArrayList
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.model_app.HistoryColumnData
import com.example.budgetvalue.model_app.PlanAndActual
import com.example.budgetvalue.model_app.Reconciliation
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject

class HistoryVM(val transactionsVM: TransactionsVM) : ViewModel() {
    val reconciliations = SourceArrayList<Reconciliation>()
    val plans = SourceArrayList<PlanAndActual>()
    // Plan comes from "saves", but there can only be 1 save per block
    // Reconciliations come from "saves"
    // Actuals comes from transactions
    val historyColumnData =
        combineLatestAsTuple(reconciliations.observable, plans.observable, transactionsVM.transactionBlocks)
            .map { (reconciliations, planAndActuals, transactionBlocks) ->
                val historyColumnDatas = arrayListOf<HistoryColumnData>()
                for (planAndActual in planAndActuals) { // TODO("Sort by startDate")
                    // # Add PlanAndActual
                    historyColumnDatas.add(HistoryColumnData(
                        "Title",
                        planAndActual.planCategoryAmounts
                    ))
                    historyColumnDatas.add(HistoryColumnData(
                        "Title",
                        planAndActual.actualCategoryAmounts
                    ))
                    // # Add Reconciliations
                    for (reconciliation in reconciliations.filter { it.localDate in planAndActual.localDatePeriod }) {
                        historyColumnDatas.add(HistoryColumnData(
                            "Title",
                            reconciliation.categoryAmounts
                        ))
                    }
                }
                historyColumnDatas
            }
            .doOnNext { logz("historyColumnData:${it}") }
            .toBehaviorSubject()
}