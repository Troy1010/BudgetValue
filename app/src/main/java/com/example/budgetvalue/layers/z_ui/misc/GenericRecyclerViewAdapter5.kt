package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerViewAdapter5<V : View>(
    val itemFactory: () -> V,
    val bindDataAction: (RecyclerView.ViewHolder, View) -> Unit,
    val getItemCount_: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : RecyclerView.ViewHolder(itemFactory()) {}

    override fun getItemCount() = getItemCount_()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        bindDataAction(holder, holder.itemView)
}