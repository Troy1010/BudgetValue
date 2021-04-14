package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

// Experimental
fun LifecycleOwner.onDestroy(lambda: () -> Unit) {
    lifecycle.addObserver(object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() { lambda() }
    })
}

// Experimental
fun LifecycleOwner.onCreate(lambda: () -> Unit) {
    lifecycle.addObserver(object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() { lambda() }
    })
}