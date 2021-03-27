package com.tminus1010.budgetvalue.aa_core.middleware.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatEditText


class MoneyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                removeTextChangedListener(this)
                // If a 3rd number is added to the right of the decimal, move the decimal right.
                if (s.contains(".") && 3 <= s.split(".").last().count())
                    s.indexOfLast { it == '.' }
                        .also { s.delete(it, it + 1); s.insert(it + 1, ".") }
                addTextChangedListener(this)
            }
        })
    }
}