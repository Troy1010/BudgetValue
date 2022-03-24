package com.tminus1010.budgetvalue.ui.all_features.model

import android.content.Context
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3

class BudgetHeaderPresentationModel(val title: String, val subTitle: String) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemHeaderWithSubtitleBinding::inflate) { vb ->
            vb.textviewHeader.text = title
            vb.textviewSubtitle.text = subTitle
        }
    }
}