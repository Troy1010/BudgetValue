package com.tminus1010.budgetvalue.history

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.app.LocalDatePeriod
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.data.repos.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.app.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
    budgetedInteractor: BudgetedInteractor,
    private val datePeriodGetter: DatePeriodGetter,
    private val currentDatePeriodRepo: CurrentDatePeriodRepo,
    private val plansRepo: PlansRepo,
    private val reconciliationRepo: ReconciliationsRepo,
) : ViewModel() {
    val activeCategories: Observable<List<Category>> =
        Observable.combineLatest(reconciliationRepo.reconciliations, plansRepo.plans, reconciliationRepo.activeReconciliationCAs, transactionsInteractor.transactionBlocks, budgetedInteractor.budgeted)
        { reconciliations, plans, activeReconciliationCAs, transactionBlocks, budgeted ->
            sequenceOf<Set<Category>>()
                .plus(reconciliations.map { it.categoryAmounts.keys })
                .plus(plans.map { it.categoryAmounts.keys })
                .plus(listOf(activeReconciliationCAs.keys))
                .plus(transactionBlocks.map { it.categoryAmounts.keys })
                .plus(listOf(budgeted.categoryAmounts.keys))
                .fold(setOf<Category>()) { acc, v -> acc + v }
                .toList()
                .sortedWith(categoryComparator)
        }


    val historyVMItems =
        Rx.combineLatest(reconciliationRepo.reconciliations, plansRepo.plans, activeReconciliationDefaultAmountUC(), reconciliationRepo.activeReconciliationCAs, transactionsInteractor.transactionBlocks, budgetedInteractor.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, activeReconciliationDefaultAmount, activeReconciliationCAs, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!datePeriodGetter.isDatePeriodValid(it.datePeriod!!)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod!!) }
                reconciliations?.forEach { blockPeriods.add(datePeriodGetter.getDatePeriod(it.localDate)) }
                plans?.forEach { blockPeriods.add(it.localDatePeriod) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryVMItem>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks?.filter { it.datePeriod == blockPeriod } // TODO("sort by sortDate")
                            ?.let { it.map { HistoryVMItem.TransactionBlockVMItem(it, currentDatePeriodRepo) } },
                        reconciliations?.filter { it.localDate in blockPeriod }
                            ?.let { it.map { HistoryVMItem.ReconciliationVMItem(it, reconciliationRepo) } },
                        plans?.filter { it.localDatePeriod.startDate in blockPeriod }
                            ?.let { it.map { HistoryVMItem.PlanVMItem(it, currentDatePeriodRepo, plansRepo) } },
                    ).flatten().also { historyColumnDatas.addAll(it) }
                }
                // ## Add Active Reconciliation
                if (activeReconciliationCAs != null && activeReconciliationDefaultAmount != null) {
                    historyColumnDatas.add(
                        HistoryVMItem.ActiveReconciliationVMItem(
                            activeReconciliationCAs,
                            activeReconciliationDefaultAmount,
                        )
                    )
                }
                // ## Add Budgeted
                if (budgeted != null) historyColumnDatas.add(HistoryVMItem.BudgetedVMItem(budgeted))
                //
                historyColumnDatas
            }
            .replayNonError(1)
            .nonLazy()
}