package com.tminus1010.budgetvalue.model_app

data class Category (
    var name: String,
    var type: Type
) {
    enum class Type { Income, Always, Reservoir, Default }
}