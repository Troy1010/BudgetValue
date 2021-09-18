package com.tminus1010.budgetvalue.all.data

import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue.all.domain.Reconciliations
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MostRecentReconciliationDate @Inject constructor(
    reconciliationsRepo: ReconciliationsRepo
) : Observable<Box<LocalDate?>>() {
    private val mostRecentReconciliationDate = reconciliationsRepo.reconciliations
        .map(::Reconciliations)
        .mapBox(Reconciliations::mostRecent)
        .mapBox { (it) -> it?.localDate }

    override fun subscribeActual(observer: Observer<in Box<LocalDate?>>) = mostRecentReconciliationDate.subscribe(observer)
}