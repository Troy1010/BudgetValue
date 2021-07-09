package com.tminus1010.budgetvalue.categories.models

enum class CategoryType {
    Special, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}