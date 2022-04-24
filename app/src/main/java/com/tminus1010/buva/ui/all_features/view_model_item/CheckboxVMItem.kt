package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.buva.databinding.ItemCheckboxBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
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