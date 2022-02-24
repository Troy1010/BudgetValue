package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toPX

// TODO: add functionality to TextVMItem, and use that instead. Figure out how it can be most easily unit-tested
data class TextPresentationModel(
    val style: Style = Style.ONE,
    val text: String? = null,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val menuPresentationModel: MenuPresentationModel? = null,
) : IHasToViewItemRecipe {
    enum class Style { ONE, TWO, HEADER }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return if (style == Style.HEADER)
            ViewItemRecipe3__(context, ItemHeaderBinding::inflate) { vb ->
                vb.root.text = text
                onClick?.also { onClick -> vb.root.setOnClickListener { onClick() } }
                onLongClick?.also { onLongClick -> vb.root.setOnLongClickListener { onLongClick(); true } }
                menuPresentationModel?.bind(vb.root)
            }
        else
            ViewItemRecipe3__(context, ItemTextViewBinding::inflate) { vb ->
                if (style == Style.TWO) {
                    vb.root.setPadding(10.toPX(context), 0, 10.toPX(context), 0)
                    vb.root.requestLayout()
                }
                vb.root.text = text
                onClick?.also { onClick -> vb.root.setOnClickListener { onClick() } }
                onLongClick?.also { onLongClick -> vb.root.setOnLongClickListener { onLongClick(); true } }
                menuPresentationModel?.bind(vb.root)
            }
    }
}