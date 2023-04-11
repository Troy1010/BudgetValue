package com.tminus1010.buva.ui.review.history

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.app.HistoryInteractor
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val currentDatePeriod: CurrentDatePeriod,
    private val transactionsInteractor: TransactionsInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val accountsRepo: AccountsRepo,
    private val historyInteractor: HistoryInteractor,
    private val throbberSharedVM: ThrobberSharedVM,
) : ViewModel() {
    // # Private
    private val activeCategories =
        historyInteractor.entireHistory.map { it.addedTogether.categoryAmounts.map { it.key }.sortedWith(categoryComparator) }

    private val historyVMItems =
        historyInteractor.entireHistory.map { entireHistory ->
            entireHistory
                .sortedBy {
                    when (it) {
                        is TransactionBlock -> it.datePeriod?.startDate
                        is Reconciliation -> it.date
                        else -> null
                    }
                }
                .map {
                    when (it) {
                        is TransactionBlock ->
                            HistoryPresentationModel.TransactionBlockPresentationModel(
                                it,
                                Domain.guessAccountsTotalInPast(it.datePeriod!!.endDate, accountsRepo.accountsAggregate.first(), transactionsInteractor.transactionBlocks.first(), reconciliationsRepo.reconciliations.first()),
                                currentDatePeriod
                            )
                        is Reconciliation ->
                            HistoryPresentationModel.ReconciliationPresentationModel(
                                it,
                                reconciliationsRepo,
                                throbberSharedVM
                            )
                        is AutomaticBalanceReconciliation ->
                            HistoryPresentationModel.BudgetedVsAccountsAutomaticReconciliationPresentationModel(
                                it
                            )
                        else -> error("Unhandled:$it")
                    }
                }
                .plus(HistoryPresentationModel.BudgetedPresentationModel(entireHistory.addedTogether))
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
                        TextPresentationModel(text1 = "Total"),
                        TextPresentationModel(text1 = "Income"),
                        TextPresentationModel(text1 = "Spends"),
                        TextPresentationModel(text1 = "Difference"),
                        TextPresentationModel(text1 = "Default"),
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
                            TextPresentationModel(text3 = historyVMItem.default),
                            *historyVMItem.amountStrings(activeCategories).map {
                                TextPresentationModel(text1 = it)
                            }.toTypedArray()
                        )
                    }.toTypedArray()
                ).reflectXY(),
                dividerMap = activeCategories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.displayType })
                    .associate { it.index to it.value.displayType.name }
                    .mapKeys { it.key + 6 }
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = false,
                colFreezeCount = 1,
                rowFreezeCount = 1,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}