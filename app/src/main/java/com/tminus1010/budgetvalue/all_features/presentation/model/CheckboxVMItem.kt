package com.tminus1010.budgetvalue.all_features.presentation.model

import android.content.Context
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemCheckboxBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

class CheckboxVMItem(
    val initialValue: Boolean? = null,
    val flow: Flow<Boolean>? = null,
    val onCheckChanged: (Boolean) -> Unit,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemCheckboxBinding::inflate) { vb ->
            if (initialValue != null) vb.checkbox.isChecked = initialValue
            if (flow != null) vb.root.bind(flow) { isChecked = it; isEnabled = it }
            vb.root.setOnCheckedChangeListener { _, isChecked -> onCheckChanged(isChecked) }
        }
    }
}