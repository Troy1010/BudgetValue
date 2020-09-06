package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class TVVerticalRecyclerViewAdapter(val context: Context, val data2d:()->List<List<TableViewCellData>>, val columnWidthsObservable: BehaviorSubject<List<Int>>) : RecyclerView.Adapter<TVVerticalRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        val rowData = data2d()[yPos]
        val view = LinearLayout(context)
        for (cellData in rowData) {
            val cellView = cellData.viewFactory()
            view.addView(cellView)
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowData = data2d()[holder.adapterPosition]
        val rowView = (holder.itemView as LinearLayout)
        for ((xPos, cellData) in rowData.withIndex()) {
            cellData.bindAction(rowView[xPos], cellData.data)
            rowView[xPos].layoutParams = LinearLayout.LayoutParams(
                columnWidthsObservable.value.getOrNull(xPos) ?: 0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
    override fun getItemCount() = data2d().size
}