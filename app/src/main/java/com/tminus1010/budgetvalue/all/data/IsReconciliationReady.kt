package com.tminus1010.budgetvalue.all.data

import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(
    latestDateOfMostRecentImport: LatestDateOfMostRecentImport,
    mostRecentReconciliation: MostRecentReconciliation,
    transactionsDomain: TransactionsDomain,
    isPlanFeatureEnabled: IsPlanFeatureEnabled,
) : Observable<Boolean>() {
    val x =
        combineLatest(latestDateOfMostRecentImport, mostRecentReconciliation, transactionsDomain.spendBlocks, isPlanFeatureEnabled.dateWhenPlanFeatureWasEnabled)
        { (latestDateOfMostRecentImport), (mostRecentReconciliation), spendBlocks, (dateWhenPlanFeatureWasEnabled) ->
            val reconciliationDate = mostRecentReconciliation?.localDate ?: dateWhenPlanFeatureWasEnabled
            if (latestDateOfMostRecentImport == null || reconciliationDate == null)
                return@combineLatest false
            val spendBlocksAfterMostRecentReconciliation =
                spendBlocks.filter { reconciliationDate < it.datePeriod.endDate }
            if (spendBlocksAfterMostRecentReconciliation.isEmpty())
                return@combineLatest false
            spendBlocksAfterMostRecentReconciliation.all { it.isFullyCategorized }
                    && spendBlocksAfterMostRecentReconciliation.any { it.datePeriod.endDate < latestDateOfMostRecentImport }
        }
            .distinctUntilChanged()
            .cache()

    override fun subscribeActual(observer: Observer<in Boolean>) = x.subscribe(observer)
}