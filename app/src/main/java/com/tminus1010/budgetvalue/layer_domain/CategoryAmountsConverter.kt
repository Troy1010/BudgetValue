package com.tminus1010.budgetvalue.layer_domain

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import java.math.BigDecimal
import javax.inject.Inject

class CategoryAmountsConverter @Inject constructor(
    private val categoryParser: ICategoryParser,
    private val moshi: Moshi
) : ICategoryAmountsConverter {
    override fun toCategoryAmount(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    override fun toString(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}