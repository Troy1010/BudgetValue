package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.buva.databinding.ItemTitledDividerBinding

class DividerVMItem(
    private val s: String,
) : ViewItemRecipeFactory {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemTitledDividerBinding::inflate) { vb ->
            vb.textview.text = s
        }
    }
}