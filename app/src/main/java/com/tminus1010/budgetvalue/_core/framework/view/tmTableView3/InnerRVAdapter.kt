package com.tminus1010.budgetvalue._core.framework.view.tmTableView3

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.framework.view.LifecycleRVAdapter2

class InnerRVAdapter(
    private val recipeGrid: RecipeGrid3,
    private val j: Int,
) : LifecycleRVAdapter2<GenViewHolder2<ViewBinding>>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder2<ViewBinding> =
        GenViewHolder2(recipeGrid.createResizedView(i, j))

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = recipeGrid[0].size

    override fun onLifecycleAttached(holder: GenViewHolder2<ViewBinding>) {
        recipeGrid[j][holder.adapterPosition].bind(holder.vb)
    }
}