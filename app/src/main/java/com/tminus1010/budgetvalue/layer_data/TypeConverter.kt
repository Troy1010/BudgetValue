package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.extensions.associate
import com.tminus1010.budgetvalue.getTypeForGson
import com.tminus1010.budgetvalue.model_data.Category
import com.google.gson.Gson
import java.math.BigDecimal
import javax.inject.Inject

class TypeConverter @Inject constructor(
    val categoryParser: ICategoryParser
) {
    fun bigDecimal(s: String): BigDecimal =
        s.toBigDecimal()

    fun string(bigDecimal: BigDecimal): String =
        bigDecimal.toString()

    fun categoryAmounts(s: String?): Map<Category, BigDecimal> {
        val reconcileCategoryAmountsReceived: Map<String, String> =
            if (s == null) emptyMap() else {
                Gson().fromJson(s, getTypeForGson<HashMap<String, String>>())
            }
        return reconcileCategoryAmountsReceived
            .associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() }
    }

    fun string(categoryAmounts: Map<Category, BigDecimal>?): String? {
        val reconcileCategoryAmountsReceived =
            categoryAmounts?.associate { it.key.name to it.value.toString() }
        return if (reconcileCategoryAmountsReceived == null) null else {
            Gson().toJson(reconcileCategoryAmountsReceived)
        }
    }
}