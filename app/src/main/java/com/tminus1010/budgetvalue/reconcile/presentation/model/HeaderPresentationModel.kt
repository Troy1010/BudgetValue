package com.tminus1010.budgetvalue.reconcile.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding

class HeaderPresentationModel(val text: String):IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemHeaderBinding::inflate) { vb ->
            vb.textview.text = text
        }
    }
}