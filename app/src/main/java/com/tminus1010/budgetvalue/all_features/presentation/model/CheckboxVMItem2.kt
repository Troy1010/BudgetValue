package com.tminus1010.budgetvalue.all_features.presentation.model

import android.content.Context
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemCheckboxBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

/**
 * This Checkbox will disable when checked.
 */
class CheckboxVMItem2(
    val initialValue: Boolean? = null,
    val flow: Flow<Boolean>? = null,
    val onChecked: () -> Unit,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemCheckboxBinding::inflate) { vb ->
            vb.root.setOnCheckedChangeListener { v, isChecked -> if (isChecked) onChecked(); v.isEnabled = !isChecked }
            if (initialValue != null) vb.checkbox.isChecked = initialValue
            if (flow != null) vb.root.bind(flow) { isChecked = it }
        }
    }
}