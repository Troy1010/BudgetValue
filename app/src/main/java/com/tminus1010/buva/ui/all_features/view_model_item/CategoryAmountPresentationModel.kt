package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.EditText
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import java.math.BigDecimal

class CategoryAmountPresentationModel(
    private val category: Category,
    private val amount: BigDecimal?,
    private val onDone: ((Category, String) -> Unit)? = null,
) : IHasToViewItemRecipe {
    val amountStr by lazy { amount?.toString() ?: "" }

    fun bind(editText: EditText) {
        editText.setText(amountStr)
        onDone?.also { onDone -> editText.onDone { onDone(category, it) } }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemMoneyEditTextBinding::inflate) { vb ->
            bind(vb.moneyedittext)
        }
    }
}