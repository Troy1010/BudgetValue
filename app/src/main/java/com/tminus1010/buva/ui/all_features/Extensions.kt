package com.tminus1010.buva.ui.all_features

import android.content.Context
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.domain.ResolutionStrategy
import com.tminus1010.buva.domain.ValidationResult

fun ResetStrategy?.toDisplayStr(): String? {
    return when (this) {
        is ResetStrategy.Basic -> this.budgetedMax?.toString()
        null -> null
    }
}

fun ResolutionStrategy?.toDisplayStr(): String? {
    return when (this) {
        is ResolutionStrategy.Basic -> this.budgetedMin?.toString()
        is ResolutionStrategy.MatchPlan -> "Match Plan"
        null -> null
    }
}

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