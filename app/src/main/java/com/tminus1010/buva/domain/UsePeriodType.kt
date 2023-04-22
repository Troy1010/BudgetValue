package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.easyCapitalize

enum class UsePeriodType {
    USE_DAY_COUNT_PERIODS, USE_CALENDAR_PERIODS;

    override fun toString(): String {
        return this.name.lowercase().easyCapitalize().replace("_", " ")
    }
}