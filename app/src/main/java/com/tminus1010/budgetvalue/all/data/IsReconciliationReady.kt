package com.tminus1010.budgetvalue.all.data

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconciliationReady @Inject constructor(

): Observable<Boolean>() {
    override fun subscribeActual(observer: Observer<in Boolean>) {
        TODO("Not yet implemented")
    }
}