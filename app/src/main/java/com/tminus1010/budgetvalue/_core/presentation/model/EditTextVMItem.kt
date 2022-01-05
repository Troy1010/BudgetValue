package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyText2
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import kotlinx.coroutines.flow.Flow

class EditTextVMItem(
    val hint: String? = null,
    val textFlow: Flow<String?>? = null,
    val text: String? = null,
    val onDone: (String) -> Unit
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemEditTextBinding::inflate) { vb ->
            vb.edittext.hint = hint
            vb.edittext.easyText2 = text
            textFlow?.also { vb.edittext.bind(textFlow) { easyText2 = it } }
            vb.edittext.onDone { onDone(it) }
        }
    }
}