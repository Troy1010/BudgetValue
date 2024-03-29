package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.CurrentDate
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.domain.MiscUtil
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.tuple.tuple
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsToDoInteractor @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
    currentDate: CurrentDate,
    reconciliationSkipInteractor: ReconciliationSkipInteractor,
    settingsRepo: SettingsRepo,
    accountsRepo: AccountsRepo,
) {
    private val planReconciliationsToDo =
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, reconciliationSkipInteractor.reconciliationSkips, settingsRepo.anchorDateOffset, ::tuple)
            .sample(50)
            .distinctUntilChanged()
            .map { (transactionBlocks, reconciliations, reconciliationSkips, anchorDateOffset) ->
                transactionBlocks
                    .map { transactionBlock ->
                        tuple(
                            transactionBlock,
                            reconciliations.find { it.date in transactionBlock.datePeriod!! },
                        )
                    }
                    .filter { (transactionBlock, reconciliation) ->
                        transactionBlock.datePeriod!!
                        (reconciliation == null)
                            .also { if (!it) logv("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c reconciliation") }
                                && (currentDate.flow.value !in transactionBlock.datePeriod)
                            .also { if (!it) logv("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c currentDate:${currentDate.flow.value} is within transactionBlock.datePeriod:${transactionBlock.datePeriod}") }
//                                && transactionBlock.isFullyImported
//                            .also { if (!it) logz("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c isFullyImported:$it") }
//                                && transactionBlock.spendBlock.isFullyCategorized
//                            .also { if (!it) logz("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c isFullyCategorized:$it") }
//                                && (currentDate.flow.value !in transactionBlock.datePeriod)
//                            .also { if (!it) logz("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's current") }
                                && (!MiscUtil.shouldSkip(reconciliationSkips, transactionBlock, anchorDateOffset))
                            .also { if (!it) logv("filtering out ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's skipped") }
                    }
                    .map { (transactionBlock) ->
                        ReconciliationToDo.PlanZ(transactionBlock)
                    }
                    .sortedBy { it.transactionBlock.datePeriod!!.startDate }
            }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    //    private val accountReconciliationsToDo =
//        combine(entireHistoryInteractor.categoryAmountsAndTotal, accountsRepo.accountsAggregate, ::tuple)
//            .flatMapLatest { (entireHistory, accountsAggregate) ->
//                if (entireHistory.total.easyEquals(accountsAggregate.total))
//                    flowOf(null)
//                else
//                    planReconciliationsToDo.map { planReconciliationsToDo ->
//                        ReconciliationToDo.Accounts(
//                            date = planReconciliationsToDo.mapNotNull { it.transactionBlock.datePeriod?.startDate }.minOrNull()?.minusDays(1)
//                                ?: LocalDate.now()
//                        )
//                    }
//            }
    val reconciliationsToDo =
        planReconciliationsToDo
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
    val currentReconciliationToDo =
        reconciliationsToDo.map { it.firstOrNull() ?: ReconciliationToDo.Anytime }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}