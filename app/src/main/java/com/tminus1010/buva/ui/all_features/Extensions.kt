package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.domain.ResolutionStrategy

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