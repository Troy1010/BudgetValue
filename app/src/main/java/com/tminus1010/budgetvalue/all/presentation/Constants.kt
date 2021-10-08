package com.tminus1010.budgetvalue.all.presentation

import com.tminus1010.budgetvalue._core.all.extensions.easyCapitalize

enum class SelectableDuration {
    BY_WEEK, BY_MONTH, BY_3_MONTHS, BY_6_MONTHS, BY_YEAR, FOREVER;

    override fun toString(): String {
        return this.name.lowercase().easyCapitalize().replace("_", " ")
    }
}

enum class UsePeriodType {
    USE_DAY_COUNT_PERIODS, USE_CALENDAR_PERIODS;

    override fun toString(): String {
        return this.name.lowercase().easyCapitalize().replace("_", " ")
    }
}