package com.tminus1010.budgetvalue._middleware.view.extensions

import android.widget.Button

fun Button.onClick(lambda: () -> Unit) {
    setOnClickListener { lambda() }
}