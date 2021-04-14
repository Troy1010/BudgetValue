package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

// Experimental
abstract class LifecycleImbuedAdapter<VB: ViewBinding> (
    private val parentLifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<GenViewHolder2<VB>>() {
    init {
        parentLifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycleMap.forEach { (_, lifecycleOwner) -> lifecycleOwner.onDestroy() }
                parentLifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    abstract fun onBindViewHolder(holder: GenViewHolder2<VB>, position: Int, lifecycleOwner: LifecycleOwner)

    var lifecycleMap = hashMapOf<Int, ExposedLifecycleOwner>()

    override fun onBindViewHolder(holder: GenViewHolder2<VB>, position: Int) {
        if (lifecycleMap[position] != null) error("Shouldn't this have already been cleared..?")
        lifecycleMap[position] = ExposedLifecycleOwner()
        lifecycleMap[position]!!.onCreate()
        onBindViewHolder(holder, position, lifecycleMap[position]!!)
    }

    override fun onViewRecycled(holder: GenViewHolder2<VB>) {
        super.onViewRecycled(holder)
        lifecycleMap[holder.adapterPosition]!!.onDestroy()
        lifecycleMap.remove(holder.adapterPosition)
    }

    override fun onViewAttachedToWindow(holder: GenViewHolder2<VB>) {
        super.onViewAttachedToWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.onStart()
        lifecycleMap[holder.adapterPosition]!!.onResume()
    }

    override fun onViewDetachedFromWindow(holder: GenViewHolder2<VB>) {
        super.onViewDetachedFromWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.onPause()
        lifecycleMap[holder.adapterPosition]!!.onStop()
    }
}