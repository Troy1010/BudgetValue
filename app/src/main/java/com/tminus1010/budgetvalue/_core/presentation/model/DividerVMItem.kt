package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding

class DividerVMItem(
    private val s: String,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTitledDividerBinding::inflate) { vb ->
            vb.textview.text = s
        }
    }
}