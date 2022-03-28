package com.tminus1010.budgetvalue.framework.android.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.databinding.ItemEmptyBinding

fun Fragment.itemEmptyRF() = ItemEmptyRecipeFactory(requireContext())

class ItemEmptyRecipeFactory(private val context: Context) {
    val inflate: (LayoutInflater) -> ItemEmptyBinding = ItemEmptyBinding::inflate
    fun create(hasHighlight: Boolean = false): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            if (hasHighlight) vb.root.setBackgroundColor(context.getColor(R.color.colorBackgroundHighlight)) // TODO: use attr
        }
    }
}