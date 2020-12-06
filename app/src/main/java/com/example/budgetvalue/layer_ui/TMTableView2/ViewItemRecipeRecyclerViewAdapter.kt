package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.layer_ui.TMTableView.bindRow
import com.example.budgetvalue.layer_ui.TMTableView.createRow

class ViewItemRecipeRecyclerViewAdapter(
    val context: Context,
    val viewItemRecipe2D: () -> List<Iterable<IViewItemRecipe>>
) : RecyclerView.Adapter<ViewItemRecipeRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        return ViewHolder(createColumn(context, viewItemRecipe2D()[yPos]))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindColumn((holder.itemView as LinearLayout), viewItemRecipe2D()[holder.adapterPosition])
    }
    override fun getItemCount() = viewItemRecipe2D().size
}