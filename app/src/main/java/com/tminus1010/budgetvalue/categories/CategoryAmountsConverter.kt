package com.tminus1010.budgetvalue.categories

import com.tminus1010.budgetvalue._core.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import javax.inject.Inject

class CategoryAmountsConverter @Inject constructor(
    private val categoryParser: CategoriesInteractor,
) {
    fun toCategoryAmounts(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    fun toJson(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}