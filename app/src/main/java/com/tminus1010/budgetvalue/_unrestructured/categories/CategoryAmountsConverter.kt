package com.tminus1010.budgetvalue._unrestructured.categories

import com.tminus1010.budgetvalue.data.service.MoshiProvider.moshi
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import javax.inject.Inject

@Deprecated("use moshi")
class CategoryAmountsConverter @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
) {
    fun toCategoryAmounts(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoriesInteractor.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    fun toJson(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}