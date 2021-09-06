package com.tminus1010.budgetvalue._core.extensions

import java.math.BigDecimal

val BigDecimal.isZero
    get() = compareTo(BigDecimal.ZERO) == 0

fun BigDecimal.easyEquals(other: BigDecimal): Boolean {
    return compareTo(other) == 0
}