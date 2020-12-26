package com.tminus1010.budgetvalue.layer_ui.misc

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.subjects.PublishSubject

open class GenericRecyclerViewAdapter3(
    var binder: Callbacks
): RecyclerView.Adapter<GenericRecyclerViewAdapter3.ViewHolder>() {
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)
    val streamDataChanged = PublishSubject.create<Unit>().also {
        this.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                it.onNext(Unit)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return binder.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return binder.getRecyclerDataSize()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binder.bindRecyclerItem(holder, holder.itemView)
    }

    interface Callbacks
    {
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        fun bindRecyclerItem(holder: ViewHolder, view: View)
        fun getRecyclerDataSize() : Int
    }
}