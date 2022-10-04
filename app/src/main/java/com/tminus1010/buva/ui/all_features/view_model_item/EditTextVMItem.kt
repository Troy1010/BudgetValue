package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.EditText
import com.tminus1010.buva.all_layers.extensions.easyText2
import com.tminus1010.buva.databinding.ItemEditTextBinding
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import kotlinx.coroutines.flow.Flow

class EditTextVMItem(
    val hint: String? = null,
    val textFlow: Flow<String?>? = null,
    val text: String? = null,
    val onDone: (String) -> Unit,
    val menuVMItems: MenuVMItems? = null,
) : IHasToViewItemRecipe {
    fun bind(editText: EditText) {
        editText.hint = hint
        editText.easyText2 = text
        textFlow?.also { editText.bind(textFlow) { if (text.toString() != it) setText(it) } }
        editText.onDone { onDone(it) }
        menuVMItems?.bind(editText)
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemEditTextBinding::inflate) { vb ->
            bind(vb.edittext)
        }
    }
}