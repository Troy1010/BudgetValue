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
        val view = LayoutInflater.from(context).inflate(params.itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = params.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        params.bindRecyclerItem(holder, holder.itemView)
    }

    interface Params {
        val itemLayout: Int
        val size: Int
        fun bindRecyclerItem(holder: ViewHolder, view: View)
    }
}