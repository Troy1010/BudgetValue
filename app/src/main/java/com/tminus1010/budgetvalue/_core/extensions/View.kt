package com.tminus1010.budgetvalue._core.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

fun <V : View, T> V.bind(liveData: LiveData<T>, lifecycle: LifecycleOwner? = null, lambda: V.(T) -> Unit) {
    val _lifecycle =
        lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view if you can, or otherwise specify a lifecycle as argument.")
    liveData.observe(_lifecycle) { lambda(it) }
}

fun <V : View, T> V.bind(observable: Observable<T>, lifecycle: LifecycleOwner? = null, lambda: V.(T) -> Unit) {
    val _lifecycle =
        lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view if you can, or otherwise specify a lifecycle as argument.")
    observable.observe(_lifecycle) { lambda(it) }
}

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

var View.easyVisibility: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }


var View.lifecycleOwner: LifecycleOwner?
    get() = findViewTreeLifecycleOwner()
    set(value) {
        setTag(androidx.lifecycle.runtime.R.id.view_tree_lifecycle_owner, value)
    }