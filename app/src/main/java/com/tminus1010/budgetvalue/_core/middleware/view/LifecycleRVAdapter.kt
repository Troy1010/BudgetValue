package com.tminus1010.budgetvalue._core.middleware.view

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

@Deprecated("Use LifecycleRVAdapter2, which automatically attaches lifecycle, and does not require a map")
abstract class LifecycleRVAdapter<VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {
    abstract fun onViewAttachedToWindow(holder: VH, lifecycle: LifecycleOwner)
    override fun onBindViewHolder(holder: VH, position: Int) { }

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