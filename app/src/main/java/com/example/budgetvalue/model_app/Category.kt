package com.example.budgetvalue.model_app

data class Category (
    var name: String,
    var type: Type
) {
    enum class Type { Income, Always, Reservoir, Default }
}