package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.buva.all_layers.extensions.easyText3
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import com.tminus1010.buva.domain.ValidationResult
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class MoneyEditVMItem(
    val text1: String? = null,
    val text2: Flow<String?>? = null,
    val validation: (String) -> ValidationResult = { ValidationResult.Success },
    val onDone: (String) -> Unit,
    val menuVMItems: MenuVMItems? = null,
) : IHasToViewItemRecipe {
    private fun getColor(context: Context, s: String) =
        context.theme.getColorByAttr(
            when (validation(s)) {
                ValidationResult.Success ->
                    R.attr.colorOnBackground
                ValidationResult.Warning ->
                    R.attr.colorOnWarning
                ValidationResult.Failure ->
                    R.attr.colorOnError
            }
        )

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemMoneyEditTextBinding::inflate) { vb ->
            if (text1 != null) vb.moneyedittext.easyText3 = text1
            if (text2 != null) vb.moneyedittext.bind(text2) { if (text.toString() != it) easyText3 = it; setTextColor(getColor(context, it ?: "")) }
            vb.moneyedittext.onDone(onDone)
            menuVMItems?.bind(vb.root)
            vb.moneyedittext.setTextColor(getColor(context, vb.moneyedittext.easyText3?.toString() ?: ""))
            vb.moneyedittext.onDone { vb.moneyedittext.setTextColor(getColor(context, it)) }
        }
    }
}