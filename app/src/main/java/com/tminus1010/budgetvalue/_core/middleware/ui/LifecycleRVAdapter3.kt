package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class LifecycleRVAdapter3<VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {
    override fun onBindViewHolder(holder: VH, position: Int) { }
    abstract fun onViewAttachedToWindow(holder: VH, lifecycle: LifecycleOwner)

    private val lifecycleMap = hashMapOf<RecyclerView.ViewHolder, ExposedLifecycleOwner>()

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        if (lifecycleMap[holder] != null) error("Shouldn't this already been removed..?")
        lifecycleMap[holder] = ExposedLifecycleOwner().apply { emitResume() }
        onViewAttachedToWindow(holder, lifecycleMap[holder]!!)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        lifecycleMap[holder]!!.emitDestroy()
        lifecycleMap.remove(holder)
    }
}