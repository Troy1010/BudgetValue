package com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model

enum class SearchType(val displayStr: String) {
    DESCRIPTION_AND_TOTAL("Description and Total"), TOTAL("Total"), DESCRIPTION("Description"), NONE("None");

    override fun toString() = displayStr
}