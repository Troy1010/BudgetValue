package com.tminus1010.budgetvalue.features.categories


interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}