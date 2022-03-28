package com.tminus1010.budgetvalue.framework.android

import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.tmcommonkotlin.misc.extensions.lifecycleOwner

abstract class LifecycleRVAdapter2<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    abstract fun onLifecycleAttached(holder: VH)
    open fun onLifecycleDetached(holder: VH) {}
    override fun onBindViewHolder(holder: VH, position: Int) {}

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.lifecycleOwner = ExposedLifecycleOwner().apply { emitResume() }
        onLifecycleAttached(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        (holder.itemView.lifecycleOwner as ExposedLifecycleOwner).emitDestroy()
        holder.itemView.lifecycleOwner = null
        onLifecycleDetached(holder)
    }
}