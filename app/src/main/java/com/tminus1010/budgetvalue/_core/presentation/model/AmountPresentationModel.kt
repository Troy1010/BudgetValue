package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import java.math.BigDecimal

class AmountPresentationModel(
    val bigDecimal: BigDecimal?,
    private val checkIfValid: (BigDecimal) -> Boolean
) : IHasToViewItemRecipe {
    val s get() = bigDecimal?.toString()
    val isValid get() = checkIfValid(bigDecimal ?: BigDecimal.ZERO)
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate) { vb ->
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