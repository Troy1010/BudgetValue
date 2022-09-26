package com.tminus1010.buva.ui.history

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.all_layers.combine
import com.tminus1010.buva.app.*
import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.buva.data.PlansRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Domain
import com.tminus1010.buva.domain.LocalDatePeriod
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.tuple.Sextuple
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
    private val reconciliationSkipInteractor: ReconciliationSkipInteractor,
    private val settingsRepo: SettingsRepo,
    private val accountsInteractor: AccountsInteractor,
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
        combine(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks, budgetedInteractor.budgeted, reconciliationSkipInteractor.reconciliationSkips, settingsRepo.anchorDateOffset, ::Sextuple)
            .flowOn(Dispatchers.Default)
            .sample(500)
            .map { (reconciliations, plans, transactionBlocks, budgeted, reconciliationSkips, anchorDateOffset) ->
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
                            .let { it.map { HistoryPresentationModel.TransactionBlockPresentationModel(it, accountsInteractor.guessAccountsTotalInPast(it), currentDatePeriod, Domain.shouldSkip(reconciliationSkips, it, anchorDateOffset), reconciliationSkipInteractor) } },
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

    // # Events
    val showPopupMenu = PublishSubject.create<Pair<View, List<MenuVMItem>>>()

    // # State
    val historyTableView =
        combine(activeCategories, historyVMItems)
        { activeCategories, historyVMItems ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextPresentationModel(text1 = ""),
                        TextPresentationModel(text1 = "Accounts\nTotal"),
                        TextPresentationModel(text1 = "Income"),
                        TextPresentationModel(text1 = "Spends"),
                        TextPresentationModel(text1 = "Difference"),
                        *activeCategories.map {
                            TextPresentationModel(text1 = it.name)
                        }.toTypedArray()
                    ),
                    *historyVMItems.map { historyVMItem ->
                        listOf(
                            BasicHeaderWithSubtitlePresentationModel(historyVMItem.title, runBlocking { historyVMItem.subTitle.first() }) { showPopupMenu.onNext(Pair(it, historyVMItem.menuVMItems)) }, // TODO("Blocking is a duct-tape solution to non-resizing frozen row")
                            TextPresentationModel(text3 = historyVMItem.accountsTotal),
                            TextPresentationModel(text3 = historyVMItem.incomeTotal),
                            TextPresentationModel(text3 = historyVMItem.spendTotal),
                            TextPresentationModel(text3 = historyVMItem.difference),
                            *historyVMItem.amountStrings(activeCategories).map {
                                TextPresentationModel(text1 = it)
                            }.toTypedArray()
                        )
                    }.toTypedArray()
                ).reflectXY(),
                dividerMap = activeCategories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 5 }
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = false,
                colFreezeCount = 1,
                rowFreezeCount = 1,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}