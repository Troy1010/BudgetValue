package com.tminus1010.budgetvalue.all.presentation.models

import java.math.BigDecimal

class ValidatedStringVMItem(val bigDecimal: BigDecimal, private val predicate: (BigDecimal) -> Boolean) {
    val s get() = bigDecimal.toString()
    val isValid get() = predicate(bigDecimal)
}