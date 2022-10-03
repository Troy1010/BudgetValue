package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.EditText
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import java.math.BigDecimal

class CategoryAmountPresentationModel(
    private val category: Category,
    private val amount: BigDecimal?,
    private val onDone: ((Category, String) -> Unit)? = null,
    private val menuVMItems: MenuVMItems? = null,
) : IHasToViewItemRecipe {
    private val amountStr by lazy { amount?.toString() ?: "" }

    fun bind(editText: EditText) {
        editText.setText(amountStr)
        onDone?.also { onDone -> editText.onDone { onDone(category, it) } }
        menuVMItems?.bind(editText)
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemMoneyEditTextBinding::inflate) { vb ->
            bind(vb.moneyedittext)
        }
    }
}