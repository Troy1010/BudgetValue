package com.tminus1010.budgetvalue._core.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

fun <T> LiveData<T>.toObservable(lifecycle: LifecycleOwner): Observable<T> =
    Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycle, this))

fun <T> LiveData<T>.observe2(lifecycle: LifecycleOwner, observer: Observer<T>) {
    Completable.fromAction {
        this.observe(lifecycle, observer)
    }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
}