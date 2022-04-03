package com.tminus1010.budgetvalue.ui.history

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.categoryComparator
import com.tminus1010.budgetvalue.app.BudgetedInteractor
import com.tminus1010.budgetvalue.app.DatePeriodService
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.CurrentDatePeriod
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.tuple.Quadruple
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    budgetedInteractor: BudgetedInteractor,
    private val datePeriodService: DatePeriodService,
    private val currentDatePeriod: CurrentDatePeriod,
    private val plansRepo: PlansRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
) : ViewModel() {
    // # Internal
    private val activeCategories =
        combine(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks, budgetedInteractor.budgeted)
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
        combine(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks, budgetedInteractor.budgeted, ::Quadruple)
            .flowOn(Dispatchers.Default)
            .sample(500)
            .map { (reconciliations, plans, transactionBlocks, budgeted) ->
                // # Define blocks
                val blockPeriods = sortedSetOf<LocalDatePeriod>(compareBy { it.startDate })
                transactionBlocks.forEach { if (!datePeriodService.isDatePeriodValid(it.datePeriod!!)) error("datePeriod was not valid:${it.datePeriod}") }
                transactionBlocks.forEach { blockPeriods.add(it.datePeriod!!) }
                reconciliations.forEach { blockPeriods.add(datePeriodService.getDatePeriod(it.localDate)) }
                plans.forEach { blockPeriods.add(it.localDatePeriod) }
                // # Define historyColumnDatas
                val historyColumnDatas = arrayListOf<HistoryPresentationModel>()
                // ## Add TransactionBlocks, Reconciliations, Plans
                for (blockPeriod in blockPeriods) {
                    listOfNotNull(
                        transactionBlocks.filter { it.datePeriod == blockPeriod } // TODO("sort by sortDate")
                            .let { it.map { HistoryPresentationModel.TransactionBlockPresentationModel(it, currentDatePeriod) } },
                        reconciliations.filter { it.localDate in blockPeriod }
                            .let { it.map { HistoryPresentationModel.ReconciliationPresentationModel(it, reconciliationsRepo) } },
                        plans.filter { it.localDatePeriod.startDate in blockPeriod }
                            .let { it.map { HistoryPresentationModel.PlanPresentationModel(it, currentDatePeriod, plansRepo) } },
                    ).flatten().also { historyColumnDatas.addAll(it) }
                }
                // ## Add Budgeted
                if (budgeted != null) historyColumnDatas.add(HistoryPresentationModel.BudgetedPresentationModel(budgeted))
                //
                historyColumnDatas
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    // # Events
    val showPopupMenu = PublishSubject.create<Pair<View, List<MenuVMItem>>>()

    // # State
    val historyTableView =
        combine(activeCategories, historyVMItems)
        { activeCategories, historyVMItems ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextPresentationModel(text1 = "Categories"),
                        TextPresentationModel(text1 = "Default"),
                        *activeCategories.map {
                            TextPresentationModel(text1 = it.name)
                        }.toTypedArray()
                    ),
                    *historyVMItems.map { historyVMItem ->
                        listOf(
                            BasicHeaderWithSubtitlePresentationModel(historyVMItem.title, runBlocking { historyVMItem.subTitle.first() }) { showPopupMenu.onNext(Pair(it, historyVMItem.menuVMItems)) }, // TODO("Duct-tape solution to non-resizing frozen row")
                            TextPresentationModel(text1 = historyVMItem.defaultAmount),
                            *historyVMItem.amountStrings(activeCategories).map {
                                TextPresentationModel(text1 = it)
                            }.toTypedArray()
                        )
                    }.toTypedArray()
                ).reflectXY(),
                dividerMap = activeCategories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 2 } // header row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = false,
                colFreezeCount = 1,
                rowFreezeCount = 1,
            )
        }
}