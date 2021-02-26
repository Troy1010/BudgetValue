package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category

interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}