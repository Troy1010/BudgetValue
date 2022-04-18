package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.all_layers.extensions.easyText2
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.buva.databinding.ItemEditTextBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

@Deprecated("use EditTextVMItem2, b/c it checks current text before setting it.")
class EditTextVMItem(
    val hint: String? = null,
    val textFlow: Flow<String?>? = null,
    val text: String? = null,
    val onDone: (String) -> Unit,
    val menuVMItems: MenuVMItems? = null,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemEditTextBinding::inflate) { vb ->
            vb.edittext.hint = hint
            vb.edittext.easyText2 = text
            textFlow?.also { vb.edittext.bind(textFlow) { easyText2 = it } }
            vb.edittext.onDone { onDone(it) }
            menuVMItems?.bind(vb.edittext)
        }
    }
}