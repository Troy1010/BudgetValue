package com.tminus1010.budgetvalue.modules.categories

import com.tminus1010.budgetvalue.modules.categories.Category


interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}