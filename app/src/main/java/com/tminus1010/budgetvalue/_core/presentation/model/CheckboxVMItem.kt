package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemCheckboxBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

class CheckboxVMItem(
    val initialValue: Boolean,
    val x: Flow<Boolean>? = null,
    val onCheckChanged: (Boolean) -> Unit,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemCheckboxBinding::inflate) { vb ->
            vb.checkbox.isChecked = initialValue
            if (x != null) vb.root.bind(x) { isChecked = it; isEnabled = it }
            vb.root.setOnCheckedChangeListener { _, isChecked -> onCheckChanged(isChecked) }
        }
    }
}