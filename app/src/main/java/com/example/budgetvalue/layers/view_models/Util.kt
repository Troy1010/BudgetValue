package com.example.budgetvalue.layers.view_models

import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.util.SourceHashMap
import java.math.BigDecimal

fun getIncomeCASourceHashMap(repo: Repo): SourceHashMap<Category, BigDecimal> {
    return SourceHashMap<Category, BigDecimal>().apply {
        putAll(repo.readIncomeCA().associate { it.category to it.amount })
    }
}