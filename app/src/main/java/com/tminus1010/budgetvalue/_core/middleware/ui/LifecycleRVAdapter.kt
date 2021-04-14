package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView

// Experimental
abstract class LifecycleRVAdapter<VH: RecyclerView.ViewHolder> (
    private val parentLifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<VH>() {
    init {
        parentLifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycleMap.forEach { (_, lifecycleOwner) -> lifecycleOwner.onDestroy() }
                parentLifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    abstract fun onBindViewHolder(holder: VH, position: Int, lifecycleOwner: LifecycleOwner)

    var lifecycleMap = hashMapOf<Int, ExposedLifecycleOwner>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (lifecycleMap[position] != null) error("Shouldn't this have already been cleared..?")
        lifecycleMap[position] = ExposedLifecycleOwner()
        lifecycleMap[position]!!.onCreate()
        onBindViewHolder(holder, position, lifecycleMap[position]!!)
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        lifecycleMap[holder.adapterPosition]!!.onDestroy()
        lifecycleMap.remove(holder.adapterPosition)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.onStart()
        lifecycleMap[holder.adapterPosition]!!.onResume()
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.onPause()
        lifecycleMap[holder.adapterPosition]!!.onStop()
    }
}