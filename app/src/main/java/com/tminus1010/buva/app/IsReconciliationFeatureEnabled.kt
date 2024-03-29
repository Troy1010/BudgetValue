package com.tminus1010.buva.app

import android.app.Application
import com.tminus1010.buva.data.ReconciliationsRepo
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Deprecated("This is not maintained due to lack of effort")
class IsReconciliationFeatureEnabled @Inject constructor(
    private val app: Application,
    reconciliationsRepo: ReconciliationsRepo,
    transactionsInteractor: TransactionsInteractor,
    isPlanFeatureEnabled: IsPlanFeatureEnabled,
) {
    val flow = flowOf(true)
    // TODO()
//    private val key = booleanPreferencesKey("isReconciliationFeatureEnabled")
//    fun setTrue() {
//        GlobalScope.launch { app.dataStore.edit { it[key] = true } }
//    }
//
//    /**
//     * If all spendBlocks after startDate are fullyCategorized, then return true.
//     * The startDate is your most recent reconciliation, or the latest date of the most recent import when plan feature was enabled.
//     */
//    private val isReconciliationReadyToBeEnabled =
//        combine(latestDateOfMostRecentImportRepo.asFlow(), reconciliationsRepo.reconciliations, transactionsInteractor.spendBlocks, isPlanFeatureEnabledUC.latestDateOfMostRecentImportWhenPlanFeatureWasEnabled.asFlow())
//        { (latestDateOfMostRecentImport), reconciliations, spendBlocks, (latestDateOfMostRecentImportWhenPlanFeatureWasEnabled) ->
//            val startDate = reconciliations.maxByOrNull { it.localDate }?.localDate ?: latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
//            if (latestDateOfMostRecentImport == null || startDate == null)
//                return@combine false
//            val spendBlocksAfterStartDate =
//                spendBlocks.filter { startDate < it.datePeriod!!.endDate }
//            if (spendBlocksAfterStartDate.isEmpty())
//                return@combine false
//            spendBlocksAfterStartDate.all { it.isFullyCategorized }
//                    && spendBlocksAfterStartDate.any { it.datePeriod!!.endDate <= latestDateOfMostRecentImport }
//        }
//
//    val flow =
//        app.dataStore.data
//            .map { it[key] ?: false }
//
//    init {
//        GlobalScope.launch {
//            isReconciliationReadyToBeEnabled.takeUntil(flow.filter { it }).filter { it }.take(1).collect { setTrue() }
//        }
//    }
}