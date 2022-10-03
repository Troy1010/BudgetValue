package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.easyEquals
import com.tminus1010.buva.data.*
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Domain
import com.tminus1010.buva.domain.Plan
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.tuple.Quadruple
import com.tminus1010.tmcommonkotlin.tuple.Quintuple
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.time.LocalDate
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
        combine(transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations, reconciliationSkipInteractor.reconciliationSkips, settingsRepo.anchorDateOffset, ::Quadruple)
            .sample(50)
            .distinctUntilChanged()
            .map { (transactionBlocks, reconciliations, reconciliationSkips, anchorDateOffset) ->
                transactionBlocks
                    .map { transactionBlock ->
                        Pair(
                            transactionBlock,
                            reconciliations.find { it.date in transactionBlock.datePeriod!! }
                        )
                    }
                    .filter { (transactionBlock, reconciliation) ->
                        (reconciliation == null)
                            .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c reconciliation") }
                                && transactionBlock.isFullyImported
                            .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c isFullyImported") }
                                && transactionBlock.spendBlock.isFullyCategorized
                            .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod?.toDisplayStr()} b/c isFullyCategorized") }
                                && (currentDate.flow.value !in transactionBlock.datePeriod!!)
                            .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's current") }
                                && (!Domain.shouldSkip(reconciliationSkips, transactionBlock, anchorDateOffset))
                            .also { if (!it) logz("filtering for ReconciliationToDo.PlanZ ${transactionBlock.datePeriod.toDisplayStr()} b/c it's skipped") }
                    }
                    .map { (transactionBlock) ->
                        ReconciliationToDo.PlanZ(transactionBlock)
                    }
                    .sortedBy { it.transactionBlock.datePeriod!!.startDate }
            }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

//    private val accountReconciliationsToDo =
//        combine(entireHistoryInteractor.categoryAmountsAndTotal, accountsRepo.accountsAggregate, ::Pair)
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