package com.tminus1010.budgetvalue.history.presentation

import android.content.Context
import android.view.View
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding

class BasicHeaderWithSubtitlePresentationModel(val title: String, val subTitle: String, val onLongClick: (View) -> Unit) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemHeaderWithSubtitleBinding::inflate) { vb ->
            vb.textviewHeader.text = title
            vb.textviewSubtitle.text = subTitle
            vb.root.setOnLongClickListener { onLongClick(vb.root); true }
        }
    }
}