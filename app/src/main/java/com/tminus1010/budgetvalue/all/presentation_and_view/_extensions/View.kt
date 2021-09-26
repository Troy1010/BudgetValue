package com.tminus1010.budgetvalue.all.presentation_and_view._extensions

import android.view.View

fun View.onClick(lambda: () -> Unit) {
    setOnClickListener { lambda() }
}