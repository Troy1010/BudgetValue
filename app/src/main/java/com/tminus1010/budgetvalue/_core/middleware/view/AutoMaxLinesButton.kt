package com.tminus1010.budgetvalue._core.middleware.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.tminus1010.budgetvalue.R

class AutoMaxLinesButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialButtonStyle,
) : MaterialButton(context, attrs, defStyleAttr) {
    override fun setText(text: CharSequence, type: BufferType) {
        maxLines = text.toString()
            // count how many whitespaces
            .trim().split(Regex("""\s""")).count() // Somehow this is always at least 1?
            // min, max
            .coerceAtLeast(1).coerceAtMost(2)
        super.setText(text, type)
    }
}