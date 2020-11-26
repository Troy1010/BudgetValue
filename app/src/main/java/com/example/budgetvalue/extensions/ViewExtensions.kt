package com.example.budgetvalue.extensions

import android.view.View
import android.view.ViewGroup

fun View.setWidth(width_: Int) {
    this.layoutParams = (this.layoutParams ?: ViewGroup.LayoutParams(-1, -1))
        .apply { width = width_ }
}