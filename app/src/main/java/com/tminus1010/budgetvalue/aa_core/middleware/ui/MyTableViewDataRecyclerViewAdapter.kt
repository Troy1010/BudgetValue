package com.tminus1010.budgetvalue.aa_core.middleware.ui

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class MyTableViewDataRecyclerViewAdapter(
    val rowViewFactory: () -> View,
    val cellViewBindAction: (View, Any) -> Unit,
    val getRowCount: () -> Int,
    val getColumnCount: () -> Int,
    val getData: () -> List<Any>
): RecyclerView.Adapter<MyTableViewDataRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(rowViewFactory())
    }

    override fun getItemCount(): Int = getRowCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        (view as LinearLayout)
        for ((i, child) in view.children.withIndex()) {
            (child as TextView)
            try {
                cellViewBindAction(child, getData()[holder.adapterPosition * getColumnCount() + i])
            } catch (e: java.lang.IndexOutOfBoundsException) {
            }
        }
    }
}