package com.tminus1010.budgetvalue._core.middleware.view

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ExposedLifecycleOwner: LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    fun emitResume() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    fun emitPause() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    fun emitStart() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START) }
    fun emitStop() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP) }
    fun emitCreate() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE) }
    fun emitDestroy() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) }
}