package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.all_layers.extensions.easyText3
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import kotlinx.coroutines.flow.Flow

class MoneyEditVMItem(
    val text1: String? = null,
    val text2: Flow<String?>? = null,
    val onDone: (String) -> Unit,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemMoneyEditTextBinding::inflate) { vb ->
            if (text1 != null) vb.moneyedittext.easyText3 = text1
            if (text2 != null) vb.moneyedittext.bind(text2) { if (text.toString() != it) easyText3 = it }
            vb.moneyedittext.onDone(onDone)
        }
    }
}