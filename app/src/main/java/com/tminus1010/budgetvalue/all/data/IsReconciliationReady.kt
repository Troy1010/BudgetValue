package com.tminus1010.budgetvalue.all.data

import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(
    mostRecentImportDate: MostRecentImportDate,
    mostRecentReconciliation: MostRecentReconciliation,
    transactionsDomain: TransactionsDomain
) : Observable<Boolean>() {
    // TODO("This logic will be okay most of the time.. but mostRecentImportDate should really be latestDateOfMostRecentImport.. and what if there are multiple TransactionBlocks?")
    val x =
        combineLatest(mostRecentImportDate, mostRecentReconciliation, transactionsDomain.transactionBlocks)
        { (mostRecentImportDate), (mostRecentReconciliation), transactionBlocks ->
            if (mostRecentImportDate == null || mostRecentReconciliation == null)
                return@combineLatest false
            val transactionBlock = transactionBlocks.find { mostRecentReconciliation.localDate in it.datePeriod } ?: return@combineLatest false
            val isTransactionBlockFullyImported = transactionBlock.datePeriod.endDate < mostRecentImportDate
            isTransactionBlockFullyImported && transactionBlock.isFullyCategorized
        }
            .distinctUntilChanged()
            .cache()

    override fun subscribeActual(observer: Observer<in Boolean>) = x.subscribe(observer)
}