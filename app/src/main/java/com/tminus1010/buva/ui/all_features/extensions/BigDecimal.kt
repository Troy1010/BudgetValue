package com.tminus1010.buva.ui.all_features.extensions

import com.tminus1010.buva.all_layers.extensions.easyEquals
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal?.toMoneyDisplayStr() =
    when {
        this == null -> null
        this.setScale(0, RoundingMode.DOWN).easyEquals(this) -> this.setScale(0).toPlainString()
        else -> this.toPlainString()
    }