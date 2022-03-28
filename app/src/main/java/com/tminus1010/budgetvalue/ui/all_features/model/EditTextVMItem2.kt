package com.tminus1010.budgetvalue.ui.all_features.model

import android.content.Context
import android.widget.EditText
import com.tminus1010.budgetvalue.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import com.tminus1010.budgetvalue.framework.android.onDone
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import kotlinx.coroutines.flow.Flow

// TODO: Untested
class EditTextVMItem2(
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