package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.easyCapitalize

enum class SelectableDuration {
    BY_WEEK, BY_MONTH, BY_3_MONTHS, BY_6_MONTHS, BY_YEAR, FOREVER;

    override fun toString(): String {
        return this.name.lowercase().easyCapitalize().replace("_", " ")
    }
}