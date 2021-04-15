package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2

class InnerRVAdapter3(
    val context: Context,
    val recipeGrid: RecipeGrid3,
    val j: Int,
) : RecyclerView.Adapter<GenViewHolder2<ViewBinding>>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder2<ViewBinding> {
        return GenViewHolder2(recipeGrid.createResizedView(i, j))
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: GenViewHolder2<ViewBinding>, position: Int) {
        recipeGrid[j][holder.adapterPosition].bind(holder.vb)
    }

    override fun getItemCount() = recipeGrid[0].size
}