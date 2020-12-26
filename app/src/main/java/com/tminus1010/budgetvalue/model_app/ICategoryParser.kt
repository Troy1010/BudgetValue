package com.tminus1010.budgetvalue.model_app

interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}