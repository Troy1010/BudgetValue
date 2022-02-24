package com.tminus1010.budgetvalue.history.presentation

import android.content.Context
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toPX

data class TextPresentationModel(
    val text: String?,
    val style: Style = Style.ONE,
) : IHasToViewItemRecipe {
    enum class Style { ONE, TWO, HEADER }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return if (style == Style.HEADER)
            ViewItemRecipe3__(context, ItemHeaderBinding::inflate) { vb ->
                vb.textview.text = text
            }
        else
            ViewItemRecipe3__(context, ItemTextViewBinding::inflate) { vb ->
                if (style == Style.TWO) {
                    vb.textview.setPadding(10.toPX(context), 0, 10.toPX(context), 0)
                    vb.textview.requestLayout()
                }
                vb.textview.text = text
            }
    }
}