package com.tminus1010.buva.ui.all_features.extensions

import com.tminus1010.buva.domain.ResolutionStrategy

fun ResolutionStrategy?.toMoneyDisplayStr(): String? {
    return when (this) {
        is ResolutionStrategy.Basic -> this.budgetedMin?.toString()
        is ResolutionStrategy.MatchPlan -> "Match Plan"
        null -> null
    }
}