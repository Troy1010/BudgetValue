package com.tminus1010.budgetvalue._core.extensions

import java.math.BigDecimal

fun BigDecimal.isEqualToZero() =
    compareTo(BigDecimal.ZERO) == 0