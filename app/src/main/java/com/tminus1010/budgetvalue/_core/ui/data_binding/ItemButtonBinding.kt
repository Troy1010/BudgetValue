package com.tminus1010.budgetvalue._core.ui.data_binding

import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding

fun ItemButtonBinding.bind(buttonRVItem: ButtonRVItem) {
    btnItem.text = buttonRVItem.title
    btnItem.setOnClickListener { buttonRVItem.onClick() }
}