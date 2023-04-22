package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.buva.all_layers.extensions.easyText
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import com.tminus1010.buva.domain.ValidationResult
import com.tminus1010.buva.ui.all_features.extensions.toColor
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class MoneyEditVMItem(
    val text1: String? = null,
    val text2: Flow<String?>? = null,
    val validation: ((String?) -> ValidationResult)? = null,
    val onDone: (String) -> Unit,
    val menuVMItems: MenuVMItems? = null,
) : ViewItemRecipeFactory {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemMoneyEditTextBinding::inflate) { vb ->
            fun setColor() {
                if (validation != null)
                    vb.moneyedittext.setTextColor(validation!!(vb.moneyedittext.easyText?.toString()).toColor(vb.moneyedittext.context))
            }
            vb.moneyedittext.easyText = text1
            setColor()
            if (text2 != null) vb.moneyedittext.bind(text2) { if (text.toString() != it) easyText = it; setColor() }
            vb.moneyedittext.onDone { onDone(it); setColor() }
            menuVMItems?.bind(vb.root)
        }
    }
}