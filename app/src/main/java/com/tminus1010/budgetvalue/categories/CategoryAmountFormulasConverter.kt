package com.tminus1010.budgetvalue.categories

import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import javax.inject.Inject

class CategoryAmountFormulasConverter @Inject constructor(
    private val categoryParser: ICategoryParser,
    private val moshi: Moshi
) {
    fun toCategoryAmountFormulas(s: String?): Map<Category, AmountFormula> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to AmountFormula.fromDTO((it.value as Any).toString()) }

    fun toJson(categoryAmounts: Map<Category, AmountFormula>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toDTO() }
            .let { moshi.toJson(it) }
}