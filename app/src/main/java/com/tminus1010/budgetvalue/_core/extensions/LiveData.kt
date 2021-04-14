package com.tminus1010.budgetvalue._core.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.rxjava3.core.Observable

fun <T> LiveData<T>.toObservable(lifecycle: LifecycleOwner): Observable<T> =
    Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycle, this))