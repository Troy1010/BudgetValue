package com.tminus1010.budgetvalue.categories.models

enum class CategoryType {
    NOT_USER_PICKABLE, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != NOT_USER_PICKABLE }
    }
}