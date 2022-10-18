package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.domain.ResetStrategy

fun ResetStrategy.toDisplayStr(): String? {
    return when (this) {
        is ResetStrategy.Basic -> this.budgetedMax?.toString()
    }
}