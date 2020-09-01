package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerViewAdapter3(
    var binder: Callbacks
): RecyclerView.Adapter<GenericRecyclerViewAdapter3.ViewHolder>() {
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

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