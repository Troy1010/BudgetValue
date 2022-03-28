package com.tminus1010.budgetvalue._unrestructured.history

import android.view.View
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.all_layers.categoryComparator
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.app.BudgetedInteractor
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.DatePeriodService
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.framework.observable.Rx
import com.tminus1010.budgetvalue.ui.all_features.model.BasicHeaderWithSubtitlePresentationModel
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.TextPresentationModel
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    budgetedInteractor: BudgetedInteractor,
    private val datePeriodService: DatePeriodService,
    private val currentDatePeriodRepo: CurrentDatePeriodRepo,
    private val plansRepo: PlansRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
) : ViewModel() {
    private val activeCategories: Observable<List<Category>> =
        Observable.combineLatest(reconciliationsRepo.reconciliations.asObservable2(), plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks2.asObservable2(), budgetedInteractor.budgeted)
        { reconciliations, plans, transactionBlocks, budgeted ->
            sequenceOf<Set<Category>>()
                .plus(reconciliations.map { it.categoryAmounts.keys })
                .plus(plans.map { it.categoryAmounts.keys })
                .plus(transactionBlocks.map { it.categoryAmounts.keys })
                .plus(listOf(budgeted.categoryAmounts.keys))
                .fold(setOf<Category>()) { acc, v -> acc + v }
                .toList()
                .sortedWith(categoryComparator)
        }


    private val historyVMItems =
        Rx.combineLatest(reconciliationsRepo.reconciliations.asObservable2(), plansRepo.plans.asObservable2(), transactionsInteractor.transactionBlocks2.asObservable2(), budgetedInteractor.budgeted)
            .observeOn(Schedulers.computation())
            .throttleLatest(500, TimeUnit.MILLISECONDS)
            .map { (reconciliations, plans, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks?.forEach { if (!datePeriodService.isDatePeriodValid(it.datePeriod!!)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks?.forEach { blockPeriods.add(it.datePeriod!!) }
                reconciliations?.forEach { blockPeriods.add(datePeriodService.getDatePeriod(it.localDate)) }
                plans?.forEach { blockPeriods.add(it.localDatePeriod) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryVMItem>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks?.filter { it.datePeriod == blockPeriod } // TODO("sort by sortDate")
                            ?.let { it.map { HistoryVMItem.TransactionBlockVMItem(it, currentDatePeriodRepo) } },
                        reconciliations?.filter { it.localDate in blockPeriod }
                            ?.let { it.map { HistoryVMItem.ReconciliationVMItem(it, reconciliationsRepo) } },
                        plans?.filter { it.localDatePeriod.startDate in blockPeriod }
                            ?.let { it.map { HistoryVMItem.PlanVMItem(it, currentDatePeriodRepo, plansRepo) } },
                    ).flatten().also { historyColumnDatas.addAll(it) }
                }
                // ## Add Budgeted
                if (budgeted != null) historyColumnDatas.add(HistoryVMItem.BudgetedVMItem(budgeted))
                //
                historyColumnDatas
            }
            .replayNonError(1)
            .nonLazy()

    // # Presentation Events
    val showPopupMenu = PublishSubject.create<Pair<View, List<MenuVMItem>>>()

    // # State
    val recipeGrid =
        Observable.combineLatest(activeCategories, historyVMItems)
        { activeCategories, historyVMItems ->
            listOf(
                listOf(
                    TextPresentationModel(text1 = "Categories"),
                    TextPresentationModel(text1 = "Default"),
                    *activeCategories.map {
                        TextPresentationModel(text1 = it.name)
                    }.toTypedArray()
                ),
                *historyVMItems.map { historyVMItem ->
                    listOf(
                        BasicHeaderWithSubtitlePresentationModel(historyVMItem.title, historyVMItem.subTitle.value?.first ?: "") { showPopupMenu.onNext(Pair(it, historyVMItem.menuVMItems)) }, // TODO("Duct-tape solution to non-resizing frozen row")
                        TextPresentationModel(text1 = historyVMItem.defaultAmount),
                        *historyVMItem.amountStrings(activeCategories).map {
                            TextPresentationModel(text1 = it)
                        }.toTypedArray()
                    )
                }.toTypedArray()
            ).reflectXY()
        }
    val dividerMap =
        activeCategories
            .map {
                it.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 2 } // header row, default row
            }
}