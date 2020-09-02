package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerViewAdapter4(
    val context : Context,
    val itemLayout: Int,
    val getItemCount_: () -> Int,
    val bindDataAction: (ViewHolder, View) -> Unit
): RecyclerView.Adapter<GenericRecyclerViewAdapter4.ViewHolder>() {
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getItemCount_()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindDataAction(holder, holder.itemView)
    }
}