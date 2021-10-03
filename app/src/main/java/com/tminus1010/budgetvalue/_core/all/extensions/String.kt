package com.tminus1010.budgetvalue._core.all.extensions

import com.tminus1010.tmcommonkotlin.core.tryOrNull
import java.math.BigDecimal


fun String.toBigDecimalOrZero(): BigDecimal =
    toBigDecimalOrNull() ?: BigDecimal.ZERO

fun String.toMoneyBigDecimal(): BigDecimal =
    toBigDecimalOrZero()
        .let { if (it.scale() == 1) it.setScale(2) else it }
        .let { tryOrNull { it.setScale(0) } ?: it } // setScale(0) throws error if digits right of the decimal are not zero.