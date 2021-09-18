package com.tminus1010.budgetvalue.all.data

import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(
    latestDateOfMostRecentImport: LatestDateOfMostRecentImport,
    mostRecentReconciliation: MostRecentReconciliation,
    transactionsDomain: TransactionsDomain
) : Observable<Boolean>() {
    val x =
        combineLatest(latestDateOfMostRecentImport, mostRecentReconciliation, transactionsDomain.transactionBlocks)
        { (latestDateOfMostRecentImport), (mostRecentReconciliation), transactionBlocks ->
            if (latestDateOfMostRecentImport == null || mostRecentReconciliation == null)
                return@combineLatest false
            val transactionBlocksAfterMostRecentReconciliation =
                transactionBlocks.filter { mostRecentReconciliation.localDate < it.datePeriod.endDate }.ifEmpty { return@combineLatest false }
            transactionBlocksAfterMostRecentReconciliation.all { it.isFullyCategorized }
                    && transactionBlocksAfterMostRecentReconciliation.any { it.datePeriod.endDate < latestDateOfMostRecentImport }
        }
            .distinctUntilChanged()
            .cache()

    override fun subscribeActual(observer: Observer<in Boolean>) = x.subscribe(observer)
}