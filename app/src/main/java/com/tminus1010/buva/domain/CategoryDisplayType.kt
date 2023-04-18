package com.tminus1010.buva.domain

enum class CategoryDisplayType {
    Special, Always, Reservoir, Unlimited;

    companion object {
        fun getPickableValues() = values().filter { it != Special }
    }
}