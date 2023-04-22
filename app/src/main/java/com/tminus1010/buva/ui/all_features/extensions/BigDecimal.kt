package com.tminus1010.buva.ui.all_features.extensions

import com.tminus1010.buva.all_layers.extensions.easyEquals
import java.math.BigDecimal

val BigDecimal.displayStr
    get() = when {
        this.setScale(0).easyEquals(this) -> this.setScale(0).toPlainString()
        else -> this.toPlainString()
    }