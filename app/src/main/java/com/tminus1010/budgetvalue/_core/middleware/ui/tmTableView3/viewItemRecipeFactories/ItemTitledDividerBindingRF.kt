package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.viewItemRecipeFactories

import android.content.Context
import android.view.LayoutInflater
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding

class ItemTitledDividerBindingRF(context: Context) : ViewItemRecipeFactory3<ItemTitledDividerBinding, String>(
    { ItemTitledDividerBinding.inflate(LayoutInflater.from(context)) },
    { d:String, vb, _ -> vb.textviewBasicCell.text = d }
)