package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter

class InnerRVAdapter3(
    parentLifecycleOwner: LifecycleOwner,
    private val recipeGrid: RecipeGrid3,
    private val j: Int,
) : LifecycleRVAdapter<GenViewHolder2<ViewBinding>>(parentLifecycleOwner) {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder2<ViewBinding> {
        return GenViewHolder2(recipeGrid.createResizedView(i, j))
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = recipeGrid[0].size
    override fun onBindViewHolder(
        holder: GenViewHolder2<ViewBinding>,
        position: Int,
        lifecycle: LifecycleOwner
    ) {
        recipeGrid[j][holder.adapterPosition].bind(holder.vb, lifecycle)
    }
}