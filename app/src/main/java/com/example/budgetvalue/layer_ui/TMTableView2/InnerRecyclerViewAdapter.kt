package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.tmcommonkotlin.logz.logz

class InnerRecyclerViewAdapter(
    val context: Context,
    val recipeGrid: RecipeGrid,
    val j: Int,
) : RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(recipeGrid.createResizedView(i, j))
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        recipeGrid[j][holder.adapterPosition].bindView(holder.itemView)
    }

    override fun getItemCount() = recipeGrid[0].size
}