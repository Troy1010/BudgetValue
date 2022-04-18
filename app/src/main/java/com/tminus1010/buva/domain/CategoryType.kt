package com.tminus1010.buva.domain

enum class CategoryType {
    Special, Always, Reservoir;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}