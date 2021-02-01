package com.tminus1010.budgetvalue.extensions

import android.view.View
import android.view.ViewGroup

fun View.easySetHeight(height: Int) {
    this.easyGetLayoutParams().height = height
}

fun View.easySetWidth(width: Int) {
    this.easyGetLayoutParams().width = width
}

fun View.easyGetLayoutParams(): ViewGroup.LayoutParams {
    return this.layoutParams ?: ViewGroup.LayoutParams(-1, -1).also { this.layoutParams = it }
}