package com.tminus1010.budgetvalue.all.data.repos

import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(
    latestDateOfMostRecentImport: LatestDateOfMostRecentImport,
    mostRecentReconciliation: MostRecentReconciliation,
    transactionsAppService: TransactionsAppService,
    isPlanFeatureEnabled: IsPlanFeatureEnabled,
) : Observable<Boolean>() {
    val isReconciliationReady =
        combineLatest(latestDateOfMostRecentImport, mostRecentReconciliation, transactionsAppService.spendBlocks, isPlanFeatureEnabled.latestDateOfMostRecentImportWhenPlanFeatureWasEnabled)
        { (latestDateOfMostRecentImport), (mostRecentReconciliation), spendBlocks, (latestDateOfMostRecentImportWhenPlanFeatureWasEnabled) ->
            val startDate = mostRecentReconciliation?.localDate ?: latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
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