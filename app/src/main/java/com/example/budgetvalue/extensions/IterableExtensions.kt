package com.example.budgetvalue.extensions

import java.math.BigDecimal


fun Iterable<BigDecimal>.sum(): BigDecimal {
    return this.fold(BigDecimal.ZERO, BigDecimal::add)
}