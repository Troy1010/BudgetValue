package com.tminus1010.budgetvalue._core.all.extensions

import android.view.View
import androidx.lifecycle.*
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <V : View, T> V.bind(liveData: LiveData<T>, lifecycle: LifecycleOwner? = null, lambda: V.(T) -> Unit) {
    val _lifecycle =
        lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view, add a lifecycle to the view, or specify a lifecycle as argument.")
    liveData.observe(_lifecycle) { lambda(it) }
}

fun <V : View, T> V.bind(observable: Observable<T>, lifecycle: LifecycleOwner? = null, lambda: V.(T) -> Unit) {
    val _lifecycle =
        lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view, add a lifecycle to the view, or specify a lifecycle as argument.")
    observable.observe(_lifecycle) { lambda(it) }
}

inline fun <V : View, reified T> V.bind(flow: Flow<T>, lifecycle: LifecycleOwner? = null, crossinline lambda: V.(T) -> Unit) {
    val _lifecycle =
        lifecycle ?: findViewTreeLifecycleOwner()
        ?: error("Could not find lifecycle. This might happen in Recyclerviews or other unattached views.\nEither attach the view, add a lifecycle to the view, or specify a lifecycle as argument.")
    flow.easyCollect(_lifecycle) { lambda(it) }
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

fun View.onClick(lambda: () -> Unit) {
    setOnClickListener { lambda() }
}