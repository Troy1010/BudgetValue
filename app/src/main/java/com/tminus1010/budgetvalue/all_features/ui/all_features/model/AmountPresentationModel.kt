package com.tminus1010.budgetvalue.all_features.ui.all_features.model

import android.content.Context
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.getColorByAttr
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import java.math.BigDecimal

class AmountPresentationModel(
    val bigDecimal: BigDecimal?,
    private val checkIfValid: (BigDecimal) -> Boolean
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