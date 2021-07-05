package com.tminus1010.budgetvalue.categories

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.extensions.fromJson
import com.tminus1010.budgetvalue._core.extensions.toJson
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import java.math.BigDecimal
import javax.inject.Inject

class CategoryAmountsConverter @Inject constructor(
    private val categoryParser: ICategoryParser,
    private val moshi: Moshi
) : ICategoryAmountsConverter {
    override fun toCategoryAmounts(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    override fun toJson(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}