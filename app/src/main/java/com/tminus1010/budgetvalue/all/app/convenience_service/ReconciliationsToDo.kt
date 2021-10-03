package com.tminus1010.budgetvalue.all.app.convenience_service

import com.tminus1010.budgetvalue.all.app.interactors.ReconciliationsToDoInteractor
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class ReconciliationsToDo @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor
) : Observable<List<ReconciliationToDo>>() {
    override fun subscribeActual(observer: Observer<in List<ReconciliationToDo>>?) =
        reconciliationsToDoInteractor.reconciliationsToDo.subscribe(observer)
}
