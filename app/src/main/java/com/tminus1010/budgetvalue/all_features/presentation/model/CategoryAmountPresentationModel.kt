package com.tminus1010.budgetvalue.all_features.presentation.model

import android.content.Context
import android.widget.EditText
import com.tminus1010.budgetvalue.all_features.framework.view.onDone
import com.tminus1010.budgetvalue.all_features.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue.all_features.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
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
        return ViewItemRecipe3__(context, ItemMoneyEditTextBinding::inflate) { vb ->
            bind(vb.moneyedittext)
        }
    }
}