package com.tminus1010.buva.all_layers.extensions

import android.widget.EditText
import com.tminus1010.tmcommonkotlin.androidx.extensions.easyGetLayoutParams


var EditText.easyText3: CharSequence?
    set(value) {
        easyGetLayoutParams() // When EditText has no layout params, this resolves error: java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference
        setText(value)
    }
    get() = text