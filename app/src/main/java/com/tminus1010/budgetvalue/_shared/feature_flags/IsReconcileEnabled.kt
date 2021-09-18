package com.tminus1010.budgetvalue._shared.feature_flags

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import javax.inject.Inject

class IsReconcileEnabled @Inject constructor(

) : Observable<Boolean>() {
    override fun subscribeActual(observer: Observer<in Boolean>) {
        just(false).subscribe(observer)
    }
}