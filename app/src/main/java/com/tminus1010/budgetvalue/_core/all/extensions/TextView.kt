package com.tminus1010.budgetvalue._core.all.extensions

import android.widget.TextView
import com.tminus1010.tmcommonkotlin.misc.extensions.easyGetLayoutParams


var TextView.easyText: String
    set(value) {
        easyGetLayoutParams() // When TextView has no layout params, this resolves error: java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference
        text = value
    }
    get() = text.toString()