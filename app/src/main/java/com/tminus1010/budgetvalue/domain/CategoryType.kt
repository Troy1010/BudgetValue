package com.tminus1010.budgetvalue.domain

enum class CategoryType {
    Special, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}