package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.buva.databinding.ItemTextViewBinding
import java.math.BigDecimal

class AmountPresentationModel(
    val bigDecimal: BigDecimal?,
    private val checkIfValid: (BigDecimal) -> Boolean = { true }
) : IHasToViewItemRecipe {
    val s get() = bigDecimal?.toString()
    val isValid get() = checkIfValid(bigDecimal ?: BigDecimal.ZERO)
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemTextViewBinding::inflate) { vb ->
            vb.textview.text = s
            vb.textview.setTextColor(
                context.theme.getColorByAttr(
                    if (isValid)
                        R.attr.colorOnBackground
                    else
                        R.attr.colorOnError
                )
            )
        }
    }
}