package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

// Experimental
class ExposedLifecycleOwner: LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    fun onResume() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    fun onPause() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    fun onStart() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START) }
    fun onStop() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP) }
    fun onCreate() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE) }
    fun onDestroy() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) }
}