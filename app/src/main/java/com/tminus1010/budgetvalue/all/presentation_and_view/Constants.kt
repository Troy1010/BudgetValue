package com.tminus1010.budgetvalue.all.presentation_and_view

import com.tminus1010.budgetvalue.all.framework.extensions.easyCapitalize

enum class SelectableDuration {
    BY_WEEK, BY_MONTH, BY_6_MONTHS, BY_YEAR, FOREVER;

    override fun toString(): String {
        return this.name.lowercase().easyCapitalize().replace("_", " ")
    }
}