package com.tminus1010.budgetvalue._core.extensions

import android.view.View
import io.reactivex.rxjava3.core.Observable

fun View.widthObservable(): Observable<Int> {
    return Observable.create { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { v: View, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            downstream.onNext(left - right)
        }
        downstream.onNext(left - right)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }
}

fun View.heightObservable(): Observable<Int> {
    return Observable.create { downstream ->
        val onLayoutChangeListener = View.OnLayoutChangeListener { v: View, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            downstream.onNext(top - bottom)
        }
        downstream.onNext(top - bottom)
        addOnLayoutChangeListener(onLayoutChangeListener)
        downstream.setCancellable { removeOnLayoutChangeListener(onLayoutChangeListener) }
    }
}