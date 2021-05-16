package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class LifecycleRVAdapter2<VH: LifecycleViewHolder>: RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) { }
    abstract fun onViewAttachedToWindow(holder: VH, lifecycle: LifecycleOwner)

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
        onViewAttachedToWindow(holder, holder.lifecycle!!)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }
}