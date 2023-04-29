package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.easyEquals
import com.tminus1010.buva.ui.all_features.extensions.toMoneyDisplayStr
import java.math.BigDecimal

class Money(s: String) : BigDecimal(s) {
    override fun toByte(): Byte {
        TODO("Not yet implemented")
    }

    override fun toChar(): Char {
        TODO("Not yet implemented")
    }

    override fun toShort(): Short {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        return (other as? BigDecimal)
            ?.let { easyEquals(it) }
            ?: false
    }

    override fun hashCode(): Int {
        return toMoneyDisplayStr().toBigDecimal().hashCode()
    }
}