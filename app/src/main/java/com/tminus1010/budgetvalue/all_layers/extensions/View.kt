package com.tminus1010.budgetvalue.all_layers.extensions

import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

fun View.widthObservable(): Observable<Int> {
    return Observable.create<Int> { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { _: View, left, _, right, _, _, _, _, _ ->
            downstream.onNext(right - left)
        }
        downstream.onNext(right - left)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }.subscribeOn(AndroidSchedulers.mainThread()) // This might not be necessary
        .filter { it != 0 } // I'm not sure why, but sometimes this emits 0
        .distinctUntilChanged()
}

fun View.heightObservable(): Observable<Int> {
    return Observable.create<Int> { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { _: View, _, top, _, bottom, _, _, _, _ ->
            downstream.onNext(bottom - top)
        }
        downstream.onNext(bottom - top)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }.subscribeOn(AndroidSchedulers.mainThread()) // This might not be necessary
        .filter { it != 0 }
        .distinctUntilChanged()
}

fun View.onClick(lambda: () -> Unit) {
    setOnClickListener { lambda() }
}