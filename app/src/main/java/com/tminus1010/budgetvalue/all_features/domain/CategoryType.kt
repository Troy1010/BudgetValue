package com.tminus1010.budgetvalue.all_features.domain

enum class CategoryType {
    Special, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}