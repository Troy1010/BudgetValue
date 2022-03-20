package com.tminus1010.budgetvalue.budgeted.presentation

import android.content.Context
import com.tminus1010.budgetvalue.all_features.framework.view.tmTableView3.IViewItemRecipe3

interface IHasToViewItemRecipe {
    fun toViewItemRecipe(context: Context): IViewItemRecipe3
}
