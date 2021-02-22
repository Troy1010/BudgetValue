package com.tminus1010.budgetvalue.model_app

import com.tminus1010.budgetvalue.model_data.Category

interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}