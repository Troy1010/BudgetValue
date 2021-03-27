package com.tminus1010.budgetvalue.history

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue._core.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HistoryVM(
    private val domainFacade: DomainFacade,
    transactionsVM: TransactionsVM,
    activeReconciliationVM: ActiveReconciliationVM,
    activeReconciliationVM2: ActiveReconciliationVM2,
    budgetedVM: BudgetedVM,
) : ViewModel() {
    val historyColumnDatas =
        Rx.combineLatest(domainFacade.reconciliations, domainFacade.plans, activeReconciliationVM2.defaultAmount, activeReconciliationVM.activeReconcileCAs, transactionsVM.transactionBlocks, budgetedVM.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, activeReconciliationDefaultAmount, activeReconciliationCAs, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!domainFacade.isDatePeriodValid(it.datePeriod)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod) }
                reconciliations?.forEach { blockPeriods.add(domainFacade.getDatePeriod(it.localDate)) }
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