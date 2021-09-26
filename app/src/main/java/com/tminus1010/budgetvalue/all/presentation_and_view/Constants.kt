package com.tminus1010.budgetvalue.all.presentation_and_view

import java.util.*

enum class SelectableDuration {
    THIS_MONTH, TWO_MONTHS_COMBINED, ONE_MONTH_AGO, TWO_MONTHS_AGO, FOREVER;

    override fun toString(): String {
        return this.name.lowercase().capitalize(Locale.ROOT).replace("_", " ")
    }
}