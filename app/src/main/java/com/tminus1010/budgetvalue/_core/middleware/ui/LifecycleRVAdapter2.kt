package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class LifecycleRVAdapter2<VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {
    private var lifecycleMap = hashMapOf<Int, ExposedLifecycleOwner>()

    override fun onBindViewHolder(holder: VH, position: Int) { }
    abstract fun onViewAttachedToWindow(holder: VH, lifecycle: LifecycleOwner)

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        if (lifecycleMap[holder.adapterPosition] != null) error("Shouldn't this already been removed..?")
        lifecycleMap[holder.adapterPosition] = ExposedLifecycleOwner()
        lifecycleMap[holder.adapterPosition]!!.emitResume()
        onViewAttachedToWindow(holder, lifecycleMap[holder.adapterPosition]!!)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.emitDestroy()
        lifecycleMap.remove(holder.adapterPosition)
    }
}