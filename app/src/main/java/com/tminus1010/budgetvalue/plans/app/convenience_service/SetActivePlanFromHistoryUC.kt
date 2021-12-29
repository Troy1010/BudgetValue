package com.tminus1010.budgetvalue.plans.app.convenience_service

import com.tminus1010.budgetvalue.plans.app.interactor.SetActivePlanFromHistoryInteractor
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import javax.inject.Inject

class SetActivePlanFromHistoryUC @Inject constructor(
    private val setActivePlanFromHistoryInteractor: SetActivePlanFromHistoryInteractor
) : Completable() {
    override fun subscribeActual(observer: CompletableObserver?) {
        setActivePlanFromHistoryInteractor.setActivePlanFromHistory.subscribe(observer)
    }
}