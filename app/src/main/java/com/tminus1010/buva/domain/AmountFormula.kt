package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Everything needed to calculate an amount
 */
sealed class AmountFormula : Parcelable {
    abstract fun calcAmount(total: BigDecimal): BigDecimal
    abstract fun isZero(): Boolean
    abstract fun toDisplayStr(): String
    abstract fun toDisplayStr2(): String

    @Parcelize
    data class Value(val amount: BigDecimal) : AmountFormula() {
        override fun calcAmount(total: BigDecimal) = amount
        override fun isZero() = amount.compareTo(BigDecimal.ZERO) == 0
        override fun toDisplayStr() = amount.toString()
        override fun toDisplayStr2() = "$${amount}"
    }

    @Parcelize
    data class Percentage(val percentage: BigDecimal) : AmountFormula() {
        override fun calcAmount(total: BigDecimal) = (total.abs() * percentage / BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)
        override fun isZero() = percentage.compareTo(BigDecimal.ZERO) == 0
        override fun toDisplayStr() = percentage.toString()
        override fun toDisplayStr2() = "$percentage%"
    }
}