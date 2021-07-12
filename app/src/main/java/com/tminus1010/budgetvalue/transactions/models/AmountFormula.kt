package com.tminus1010.budgetvalue.transactions.models

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Everything needed to calculate an amount
 */
data class AmountFormula(
    // Could be a value or a percentage, depending on isPercentage
    val amount: BigDecimal,
    val percentage: BigDecimal,
) {
    fun calcAmount(total: BigDecimal): BigDecimal =
        (amount + total.abs() * percentage / BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)

    fun toDTO(): String =
        "$amount:$percentage"

    companion object {
        fun fromDTO(s: String) =
            s.split(":")
                .let {
                    AmountFormula(
                        amount = it[0].toBigDecimal(),
                        percentage = it[1].toBigDecimal()
                    )
                }
    }
}