package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.buva.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3

class BudgetHeaderPresentationModel(val title: String, val subTitle: String) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemHeaderWithSubtitleBinding::inflate) { vb ->
            vb.textviewHeader.text = title
            vb.textviewSubtitle.text = subTitle
        }
    }
}