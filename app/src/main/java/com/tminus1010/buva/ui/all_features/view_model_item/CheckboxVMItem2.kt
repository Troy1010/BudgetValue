package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.buva.databinding.ItemCheckboxBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

/**
 * This Checkbox will disable when checked.
 */
class CheckboxVMItem2(
    val initialValue: Boolean? = null,
    val flow: Flow<Boolean>? = null,
    val onChecked: () -> Unit,
) : ViewItemRecipeFactory {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemCheckboxBinding::inflate) { vb ->
            vb.root.setOnCheckedChangeListener { v, isChecked -> if (isChecked) onChecked(); v.isEnabled = !isChecked }
            if (initialValue != null) vb.checkbox.isChecked = initialValue
            if (flow != null) vb.root.bind(flow) { isChecked = it }
        }
    }
}