package com.tminus1010.budgetvalue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerViewAdapter6(
    var context: Context,
    var params: Params,
) : RecyclerView.Adapter<GenericRecyclerViewAdapter6.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(context).inflate(params.itemLayout, parent, false)
            .let { ViewHolder(it) }
    }

    override fun getItemCount() = params.getItemCount()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        params.bindRecyclerItem(holder, holder.itemView)
    }

    interface Params {
        val itemLayout: Int
        fun getItemCount(): Int
        fun bindRecyclerItem(holder: ViewHolder, view: View)
    }
}