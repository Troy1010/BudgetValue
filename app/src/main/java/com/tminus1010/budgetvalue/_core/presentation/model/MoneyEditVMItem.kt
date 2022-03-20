package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import kotlinx.coroutines.flow.Flow

class MoneyEditVMItem(
    val text1: String? = null,
    val text2: Flow<String?>? = null,
    val onDone: (String) -> Unit,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemMoneyEditTextBinding::inflate) { vb ->
            if (text1 != null) vb.moneyedittext.easyText2 = text1
            if (text2 != null) vb.moneyedittext.bind(text2) { easyText2 = it }
            vb.moneyedittext.onDone(onDone)
        }
    }
}