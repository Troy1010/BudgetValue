package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.EditText
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.buva.all_layers.extensions.easyText3
import com.tminus1010.buva.databinding.ItemEditTextBinding
import com.tminus1010.buva.domain.ValidationResult
import com.tminus1010.buva.ui.all_features.toColor
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class EditTextVMItem(
    val hint: String? = null,
    val textFlow: Flow<String?>? = null,
    val text: String? = null,
    val onDone: (String) -> Unit,
    val menuVMItems: MenuVMItems? = null,
    val validation: ((String?) -> ValidationResult)? = null,
) : ViewItemRecipeFactory {
    fun bind(editText: EditText) {
        fun setColor() {
            if (validation != null)
                editText.setTextColor(validation!!(editText.easyText3?.toString()).toColor(editText.context))
        }
        editText.hint = hint
        editText.easyText3 = text
        setColor()
        textFlow?.also { editText.bind(textFlow) { if (text.toString() != it) setText(it); setColor() } }
        editText.onDone { onDone(it); setColor() }
        menuVMItems?.bind(editText)
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemEditTextBinding::inflate) { vb ->
            bind(vb.edittext)
        }
    }
}