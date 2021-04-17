package com.tminus1010.budgetvalue._core.extensions

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

fun <V : View, T> V.bind(liveData: LiveData<T>, lifecycle: LifecycleOwner? = null, lambda: V.(T) -> Unit, ) {
    val _lifecycle = lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view if you can, or otherwise specify a lifecycle as argument.")
    liveData.observe(_lifecycle) { lambda(it) }
}

fun TextView.bindText(liveData: LiveData<String>) {
    bind(liveData) { easyText = it }
}

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