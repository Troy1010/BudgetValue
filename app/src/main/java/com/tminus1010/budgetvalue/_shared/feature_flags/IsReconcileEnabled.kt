package com.tminus1010.budgetvalue._shared.feature_flags

import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class IsReconcileEnabled @Inject constructor() {
    operator fun invoke(): Observable<Boolean> {
        return Observable.just(false)
    }
}