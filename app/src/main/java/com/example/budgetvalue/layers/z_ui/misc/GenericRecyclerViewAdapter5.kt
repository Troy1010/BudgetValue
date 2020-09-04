package com.example.budgetvalue.layers.z_ui.misc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tmcommonkotlin.logz

open class GenericRecyclerViewAdapter5<V : View>(
    val itemFactory: () -> V,
    val bindDataAction: (RecyclerView.ViewHolder, View) -> Unit,
    val getItemCount_: () -> Int
) : RecyclerView.Adapter<GenericRecyclerViewAdapter5.ViewHolder>() {
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = itemFactory()
        return ViewHolder(view)
    }

    override fun getItemCount() = getItemCount_()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindDataAction(holder, holder.itemView)
    }


}