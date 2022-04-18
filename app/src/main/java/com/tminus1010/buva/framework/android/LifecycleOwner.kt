package com.tminus1010.buva.framework.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

// Experimental
fun LifecycleOwner.onDestroy(lambda: () -> Unit) {
    lifecycle.addObserver(object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() { lambda(); lifecycle.removeObserver(this) }
    })
}

// Experimental
fun LifecycleOwner.onCreate(lambda: () -> Unit) {
    lifecycle.addObserver(object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() { lambda(); lifecycle.removeObserver(this) }
    })
}