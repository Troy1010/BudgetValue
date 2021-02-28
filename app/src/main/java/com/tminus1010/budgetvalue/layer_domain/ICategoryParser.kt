package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Category


interface ICategoryParser {
    fun parseCategory(categoryName: String): Category
}