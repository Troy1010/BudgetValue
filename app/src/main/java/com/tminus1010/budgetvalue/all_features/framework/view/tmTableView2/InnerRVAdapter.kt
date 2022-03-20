package com.tminus1010.budgetvalue.all_features.framework.view.tmTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class InnerRVAdapter(
    val context: Context,
    val recipeGrid: RecipeGrid,
    val j: Int,
) : RecyclerView.Adapter<InnerRVAdapter.ViewHolder>() {
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