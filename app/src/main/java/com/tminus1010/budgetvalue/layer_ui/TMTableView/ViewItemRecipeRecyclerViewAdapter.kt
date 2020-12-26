package com.tminus1010.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class ViewItemRecipeRecyclerViewAdapter(
    val context: Context,
    val viewItemRecipe2D: () -> List<Iterable<IViewItemRecipe>>,
    val columnWidths: List<Int>
) : RecyclerView.Adapter<ViewItemRecipeRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        return ViewHolder(createRow(context, viewItemRecipe2D()[yPos]))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindRow((holder.itemView as LinearLayout), viewItemRecipe2D()[holder.adapterPosition], columnWidths)
    }
    override fun getItemCount() = viewItemRecipe2D().size
}