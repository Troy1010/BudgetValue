package com.tminus1010.budgetvalue.all_layers.extensions

import android.widget.EditText
import com.tminus1010.tmcommonkotlin.misc.extensions.easyGetLayoutParams


@Deprecated("use easyText2")
var EditText.easyText: String
    set(value) {
        easyGetLayoutParams() // When EditText has no layout params, this resolves error: java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference
        setText(value)
    }
    get() = text.toString()

@Deprecated("use easyText3")
var EditText.easyText2: String?
    set(value) {
        easyGetLayoutParams() // When EditText has no layout params, this resolves error: java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference
        setText(value)
    }
    get() = text.toString()

var EditText.easyText3: CharSequence?
    set(value) {
        easyGetLayoutParams() // When EditText has no layout params, this resolves error: java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference
        setText(value)
    }
    get() = text