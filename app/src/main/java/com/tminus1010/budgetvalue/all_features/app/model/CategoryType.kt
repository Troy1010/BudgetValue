package com.tminus1010.budgetvalue.all_features.app.model

enum class CategoryType {
    Special, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}