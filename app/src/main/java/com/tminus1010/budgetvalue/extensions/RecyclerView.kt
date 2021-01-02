package com.tminus1010.budgetvalue.extensions

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.clearItemDecorations() {
    for (i in (this.itemDecorationCount-1) downTo 0 ) {
        this.removeItemDecorationAt(i)
    }
}