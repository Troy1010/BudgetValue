package com.tminus1010.budgetvalue.layer_data


import com.google.gson.Gson
import com.tminus1010.budgetvalue.getTypeForGson
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.associate
import java.math.BigDecimal
import javax.inject.Inject

class TypeConverter @Inject constructor(
    val categoryParser: ICategoryParser,
) {
    fun categoryAmounts(s: String?): Map<Category, BigDecimal> =
        if (s == null) emptyMap() else
            Gson().fromJson<Map<String, String>>(s, getTypeForGson<HashMap<String, String>>())
                .associate { categoryParser.parseCategory(it.key) to (it.value as Any).toString().toBigDecimal() }

    fun string(categoryAmounts: Map<Category, BigDecimal>): String =
        categoryAmounts
            .associate { it.key.name to it.value.toString() }
            .let { Gson().toJson(it) }
}