package com.tminus1010.budgetvalue._core.all.extensions

import java.math.BigDecimal

val BigDecimal.isZero
    get() = compareTo(BigDecimal.ZERO) == 0

val BigDecimal.isPositive
    get() = compareTo(BigDecimal.ZERO) == 1

val BigDecimal.isNegative
    get() = compareTo(BigDecimal.ZERO) == -1

fun BigDecimal.easyEquals(other: BigDecimal): Boolean {
    return compareTo(other) == 0
}