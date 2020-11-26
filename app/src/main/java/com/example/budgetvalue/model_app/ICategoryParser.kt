package com.example.budgetvalue.model_app

interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}