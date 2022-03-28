package com.tminus1010.budgetvalue._unrestructured.reconcile.data

import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.app.IsPlanFeatureEnabledUC
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.LatestDateOfMostRecentImportRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(
    latestDateOfMostRecentImportRepo: LatestDateOfMostRecentImportRepo,
    reconciliationsRepo: ReconciliationsRepo,
    transactionsInteractor: TransactionsInteractor,
    isPlanFeatureEnabledUC: IsPlanFeatureEnabledUC,
) : Observable<Boolean>() {
    /**
     * If all spendBlocks after startDate are fullyCategorized, then return true.
     * The startDate is your most recent reconciliation, or the latest date of the most recent import when plan feature was enabled.
     */
    val isReconciliationReady =
        combineLatest(latestDateOfMostRecentImportRepo, reconciliationsRepo.reconciliations.asObservable2(), transactionsInteractor.spendBlocks.asObservable2(), isPlanFeatureEnabledUC.latestDateOfMostRecentImportWhenPlanFeatureWasEnabled)
        { (latestDateOfMostRecentImport), reconciliations, spendBlocks, (latestDateOfMostRecentImportWhenPlanFeatureWasEnabled) ->
            val startDate = reconciliations.maxByOrNull { it.localDate }?.localDate ?: latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
            if (latestDateOfMostRecentImport == null || startDate == null)
                return@combineLatest false
            val spendBlocksAfterStartDate =
                spendBlocks.filter { startDate < it.datePeriod!!.endDate }
            if (spendBlocksAfterStartDate.isEmpty())
                return@combineLatest false
            spendBlocksAfterStartDate.all { it.isFullyCategorized }
                    && spendBlocksAfterStartDate.any { it.datePeriod!!.endDate <= latestDateOfMostRecentImport }
        }
            .distinctUntilChanged()
            .cache()

    override fun subscribeActual(observer: Observer<in Boolean>) = isReconciliationReady.subscribe(observer)
}