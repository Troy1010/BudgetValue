package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class LifecycleRVAdapterWrapper<VB: ViewBinding> (
    private val parentLifecycleOwner: LifecycleOwner,
    private val rvAdapter: RecyclerView.Adapter<GenViewHolder2<VB>>
): RecyclerView.Adapter<GenViewHolder2<VB>>() {
    val lifecyclesToDestroy = mutableListOf<GenViewHolder2<VB>>()
    init {
        parentLifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecyclesToDestroy.forEach { it.onDestroy() }
                parentLifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenViewHolder2<VB> =
        rvAdapter.onCreateViewHolder(parent, viewType)
            .also { holder -> holder.onCreate(); lifecyclesToDestroy += holder }

    override fun onBindViewHolder(holder: GenViewHolder2<VB>, position: Int) {
        holder.onStart()
        rvAdapter.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: GenViewHolder2<VB>, position: Int, payloads: MutableList<Any>) {
        holder.onStart()
        rvAdapter.onBindViewHolder(holder, position, payloads)
    }

    override fun onViewRecycled(holder: GenViewHolder2<VB>) {
        holder.onStop()
        rvAdapter.onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: GenViewHolder2<VB>) =
        rvAdapter.onViewAttachedToWindow(holder)

    override fun onViewDetachedFromWindow(holder: GenViewHolder2<VB>) =
        rvAdapter.onViewDetachedFromWindow(holder)

    override fun getItemCount(): Int = rvAdapter.itemCount

    override fun getItemId(position: Int): Long = rvAdapter.getItemId(position)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) =
        rvAdapter.onAttachedToRecyclerView(recyclerView)

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) =
        rvAdapter.onDetachedFromRecyclerView(recyclerView)

    override fun onFailedToRecycleView(holder: GenViewHolder2<VB>): Boolean =
        rvAdapter.onFailedToRecycleView(holder)

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) =
        rvAdapter.registerAdapterDataObserver(observer)

    override fun setHasStableIds(hasStableIds: Boolean) =
        rvAdapter.setHasStableIds(hasStableIds)

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) =
        rvAdapter.unregisterAdapterDataObserver(observer)

    override fun getItemViewType(position: Int): Int =
        rvAdapter.getItemViewType(position)
}