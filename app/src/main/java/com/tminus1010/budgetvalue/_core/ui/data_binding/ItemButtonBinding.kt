package com.tminus1010.budgetvalue._core.ui.data_binding

import com.tminus1010.budgetvalue._core.middleware.ui.ButtonPartial
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding

fun ItemButtonBinding.bind(buttonPartial: ButtonPartial) {
    btnItem.text = buttonPartial.title
    btnItem.setOnClickListener { buttonPartial.onClick() }
}