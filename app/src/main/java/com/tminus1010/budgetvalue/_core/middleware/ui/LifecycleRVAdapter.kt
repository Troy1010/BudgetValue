package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

// Experimental
abstract class LifecycleRVAdapter<VH: RecyclerView.ViewHolder> (
    parentLifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<VH>() {
    init {
        parentLifecycleOwner.onDestroy {
            lifecycleMap.forEach { (_, lifecycleOwner) -> lifecycleOwner.emitDestroy() }
        }
    }

    abstract fun onBindViewHolder(holder: VH, position: Int, lifecycleOwner: LifecycleOwner)

    var lifecycleMap = hashMapOf<Int, ExposedLifecycleOwner>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (lifecycleMap[position] != null) error("Shouldn't this have already been cleared..?")
        lifecycleMap[position] = ExposedLifecycleOwner()
        lifecycleMap[position]!!.emitCreate()
        onBindViewHolder(holder, position, lifecycleMap[position]!!)
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        lifecycleMap[holder.adapterPosition]!!.emitDestroy()
        lifecycleMap.remove(holder.adapterPosition)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.emitStart()
        lifecycleMap[holder.adapterPosition]!!.emitResume()
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        lifecycleMap[holder.adapterPosition]!!.emitPause()
        lifecycleMap[holder.adapterPosition]!!.emitStop()
    }
}