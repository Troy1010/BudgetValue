package com.tminus1010.budgetvalue.categories

import com.tminus1010.budgetvalue.categories.models.Category


interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}