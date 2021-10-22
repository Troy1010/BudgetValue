package com.tminus1010.budgetvalue.reconcile.app.convenience_service

import com.tminus1010.budgetvalue.reconcile.app.interactor.ReconciliationsToDoInteractor
import com.tminus1010.budgetvalue.reconcile.domain.ReconciliationToDo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class ReconciliationsToDoUC @Inject constructor(
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor
) : Observable<List<ReconciliationToDo>>() {
    override fun subscribeActual(observer: Observer<in List<ReconciliationToDo>>?) {
        reconciliationsToDoInteractor.reconciliationsToDo.subscribe(observer)
    }
}
