package com.tminus1010.budgetvalue.layer_data


import com.tminus1010.budgetvalue.extensions.fromJson
import com.tminus1010.budgetvalue.extensions.toJson
import com.tminus1010.budgetvalue.layer_domain.ICategoryParser
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.moshi
import com.tminus1010.tmcommonkotlin.rx.extensions.associate
import java.math.BigDecimal
import javax.inject.Inject

class TypeConverter @Inject constructor(
    val categoryParser: ICategoryParser,
) {
    fun categoryAmounts(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            moshi.fromJson<Map<String, String>>(s)
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    fun string(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { moshi.toJson(it) }
}