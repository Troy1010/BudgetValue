package com.tminus1010.budgetvalue.budgeted.presentation

import android.content.Context
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding

class BudgetHeaderPresentationModel(val title: String, val subTitle: String) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemHeaderWithSubtitleBinding::inflate) { vb ->
            vb.textviewHeader.text = title
            vb.textviewSubtitle.text = subTitle
        }
    }
}