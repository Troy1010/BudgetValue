package com.tminus1010.budgetvalue.features_shared

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.Rx
import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.features.transactions.TransactionsVM
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.middleware.LocalDatePeriod
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HistoryVM(
    private val domain: Domain,
    transactionsVM: TransactionsVM,
    activeReconciliationVM: ActiveReconciliationVM,
    activeReconciliationVM2: ActiveReconciliationVM2,
    budgetedVM: BudgetedVM,
) : ViewModel() {
    val historyColumnDatas =
        Rx.combineLatest(domain.reconciliations, domain.plans, activeReconciliationVM2.defaultAmount, activeReconciliationVM.activeReconcileCAs, transactionsVM.transactionBlocks, budgetedVM.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, activeReconciliationDefaultAmount, activeReconciliationCAs, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!domain.isDatePeriodValid(it.datePeriod)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod) }
                reconciliations?.forEach { blockPeriods.add(domain.getDatePeriod(it.localDate)) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<IHistoryColumn>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks?.filter { it.datePeriod == blockPeriod },
                        reconciliations?.filter { it.localDate in blockPeriod },
                        plans?.filter { it.localDatePeriod.blockingFirst().startDate in blockPeriod }
                    ).flatten().also { historyColumnDatas.addAll(it) }
                }
                // ## Add Active Reconciliation
                if (activeReconciliationCAs != null && activeReconciliationDefaultAmount != null) {
                    historyColumnDatas.add(
                        HistoryColumn(
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