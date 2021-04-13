package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class GenViewHolder(v: View) : RecyclerView.ViewHolder(v)
class GenViewHolder2<T : ViewBinding>(val vb: T) : RecyclerView.ViewHolder(vb.root), LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    fun onStart() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START) }
    fun onStop() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP) }
    fun onCreate() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE) }
    fun onDestroy() { lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY) }
}