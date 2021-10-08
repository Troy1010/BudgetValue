package com.tminus1010.budgetvalue.reconcile.data

import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue.all.domain.models.Reconciliations
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.app.Reconciliation
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MostRecentReconciliation @Inject constructor(
    reconciliationsRepo: ReconciliationsRepo
) : Observable<Box<Reconciliation?>>() {
    private val mostRecentReconciliation = reconciliationsRepo.reconciliations
        .map(::Reconciliations)
        .mapBox(Reconciliations::mostRecent)

    override fun subscribeActual(observer: Observer<in Box<Reconciliation?>>) = mostRecentReconciliation.subscribe(observer)
}