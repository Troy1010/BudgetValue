package com.tminus1010.budgetvalue.all_layers.extensions

import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.ColorInt

@ColorInt
fun Theme.getColorByAttr(attrID: Int): Int {
    return TypedValue()
        .also { resolveAttribute(attrID, it, true) }
        .data
}