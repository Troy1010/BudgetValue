package com.tminus1010.budgetvalue.transactions.models

enum class SearchType(val displayStr: String) {
    DESCRIPTION_AND_TOTAL("Description and Total"), TOTAL("Total"), DESCRIPTION("Description");

    override fun toString() = displayStr
}