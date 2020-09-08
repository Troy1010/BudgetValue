package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject

class RecyclerViewAdapter(val context: Context, val recipe2D:()->List<List<ICellRecipe>>, val columnWidthsObservable: BehaviorSubject<List<Int>>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        val rowData = recipe2D()[yPos]
        val view = LinearLayout(context)
        for (cellData in rowData) {
            val cellView = cellData.viewFactory()
            view.addView(cellView)
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowData = recipe2D()[holder.adapterPosition]
        val rowView = (holder.itemView as LinearLayout)
        for ((xPos, cellData) in rowData.withIndex()) {
            cellData.bindAction(rowView[xPos], cellData.data)
            rowView[xPos].layoutParams = LinearLayout.LayoutParams(
                columnWidthsObservable.value.getOrNull(xPos) ?: 0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }
    override fun getItemCount() = recipe2D().size
}