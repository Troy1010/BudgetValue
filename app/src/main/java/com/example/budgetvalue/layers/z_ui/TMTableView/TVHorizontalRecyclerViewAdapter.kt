package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.util.intrinsicHeight2
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject

class TVHorizontalRecyclerViewAdapter(val context: Context, val rowData:List<TableViewCellData>, val columnWidthsObservable:BehaviorSubject<List<Int>>) : RecyclerView.Adapter<TVHorizontalRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, xPos: Int): ViewHolder {
        return ViewHolder(rowData[xPos].viewFactory())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val xPos = holder.adapterPosition
        val view = holder.itemView
        rowData[xPos].bindAction(view,rowData[xPos].data)
        logz("xPos:${xPos}")
        logz("..columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 10:${columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 10}")
        logz("..view.intrinsicHeight2:${view.intrinsicHeight2}")
        view.layoutParams = RecyclerView.LayoutParams(
            columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 10,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemCount()= rowData.size
}
