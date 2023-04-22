package com.tminus1010.buva.ui.all_features.extensions

import android.content.Context
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.domain.ValidationResult


fun ValidationResult.toColor(context: Context): Int {
    return context.theme.getColorByAttr(
        when (this) {
            ValidationResult.Success ->
                R.attr.colorOnBackground
            is ValidationResult.Warning ->
                R.attr.colorOnWarning
            is ValidationResult.Failure ->
                R.attr.colorOnError
        }
    )
}