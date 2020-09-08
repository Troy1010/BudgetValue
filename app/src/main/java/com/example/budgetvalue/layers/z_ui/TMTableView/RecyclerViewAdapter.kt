package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.subjects.BehaviorSubject

class RecyclerViewAdapter(val context: Context, val recipe2D:()->List<List<ICellRecipe>>, val columnWidthsObservable: BehaviorSubject<List<Int>>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        return ViewHolder(createRow(context, recipe2D()[yPos]))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindRow((holder.itemView as LinearLayout), recipe2D()[holder.adapterPosition], columnWidthsObservable)
    }
    override fun getItemCount() = recipe2D().size
}