package com.tminus1010.budgetvalue._core.extensions

import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

fun View.widthObservable(): Observable<Int> {
    return Observable.create<Int> { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { v: View, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            downstream.onNext(right - left)
        }
        downstream.onNext( right - left)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }.subscribeOn(AndroidSchedulers.mainThread()) // This might not be necessary
        .distinctUntilChanged()
}

fun View.heightObservable(): Observable<Int> {
    return Observable.create<Int> { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { v: View, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            downstream.onNext(bottom - top)
        }
        downstream.onNext(bottom - top)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }.subscribeOn(AndroidSchedulers.mainThread()) // This might not be necessary
        .distinctUntilChanged()
}