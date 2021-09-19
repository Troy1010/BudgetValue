package com.tminus1010.budgetvalue._core.middleware.view.tmTableView3

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.extensions.lifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.view.LifecycleRVAdapter

class InnerRVAdapter(
    private val recipeGrid: RecipeGrid3,
    private val j: Int,
) : LifecycleRVAdapter<GenViewHolder2<ViewBinding>>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder2<ViewBinding> =
        GenViewHolder2(recipeGrid.createResizedView(i, j))

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = recipeGrid[0].size

    override fun onViewAttachedToWindow(holder: GenViewHolder2<ViewBinding>, lifecycle: LifecycleOwner) {
        holder.vb.root.lifecycleOwner = lifecycle
        recipeGrid[j][holder.adapterPosition].bind(holder.vb)
    }
}