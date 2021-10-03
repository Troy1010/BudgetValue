package com.tminus1010.budgetvalue.reconcile.presentation.model

import android.widget.EditText
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

class CategoryAmountVMItem(
    private val category: Category,
    private val amount: BigDecimal,
    private val onDone: ((Category, String) -> Unit)? = null,
) {
    val amountStr by lazy { amount.toString() }

    fun bind(editText: EditText) {
        editText.setText(amountStr)
        onDone?.also { onDone -> editText.onDone { onDone(category, it) } }
    }
}

fun EditText.bind(categoryAmountVMItem: CategoryAmountVMItem) {
    categoryAmountVMItem.bind(this)
}