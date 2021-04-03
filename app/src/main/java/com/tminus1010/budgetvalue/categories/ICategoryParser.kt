package com.tminus1010.budgetvalue.categories


interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}