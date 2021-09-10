package com.tminus1010.budgetvalue.transactions.models

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Everything needed to calculate an amount
 */
sealed class AmountFormula {
    abstract fun calcAmount(total: BigDecimal): BigDecimal
    abstract fun isZero(): Boolean
    abstract fun toDTO(): String
    abstract fun toDisplayStr(): String
    abstract fun toDisplayStr2(): String
    data class Value(val amount: BigDecimal) : AmountFormula() {
        override fun calcAmount(total: BigDecimal) = amount
        override fun isZero() = amount.compareTo(BigDecimal.ZERO) == 0
        override fun toDTO() = "$amount:Value"
        override fun toDisplayStr() = amount.toString()
        override fun toDisplayStr2() = "$${amount}"

        companion object {
            val ZERO = Value(BigDecimal.ZERO)
        }
    }

    data class Percentage(val percentage: BigDecimal) : AmountFormula() {
        override fun calcAmount(total: BigDecimal) = (total.abs() * percentage / BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)
        override fun isZero() = percentage.compareTo(BigDecimal.ZERO) == 0
        override fun toDTO() = "$percentage:Percentage"
        override fun toDisplayStr() = percentage.toString()
        override fun toDisplayStr2() = "$percentage%"
    }

    companion object {
        fun fromDTO(s: String) =
            s.split(":")
                .let {
                    when (it[1]) {
                        "Value" -> Value(it[0].toBigDecimal())
                        "Percentage" -> Percentage(it[0].toBigDecimal())
                        else -> error("Unhandled string")
                    }
                }
    }
}