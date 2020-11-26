package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewRecipeAdapter(
    val context: Context,
    val recipe2D: () -> List<List<IRecipe>>,
    val columnWidths: List<Int>
) : RecyclerView.Adapter<RecyclerViewRecipeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        return ViewHolder(createRow(context, recipe2D()[yPos]))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindRow((holder.itemView as LinearLayout), recipe2D()[holder.adapterPosition], columnWidths)
    }
    override fun getItemCount() = recipe2D().size
}