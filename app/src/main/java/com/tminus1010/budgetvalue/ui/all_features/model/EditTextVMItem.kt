package com.tminus1010.budgetvalue.ui.all_features.model

import android.content.Context
import com.tminus1010.budgetvalue.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.framework.view.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

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