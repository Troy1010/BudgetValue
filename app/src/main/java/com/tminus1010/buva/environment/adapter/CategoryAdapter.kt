package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmountFormulas
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal

class CategoryAdapter constructor(
    private val userCategoryMap: Map<String, Category>,
) {
    /**
     * [Category]
     */
    @ToJson
    fun toJson(x: Category): String =
        x.name

    @FromJson
    fun fromJson1(categoryName: String): Category {
        if (categoryName == Category.DEFAULT.name) error("Should never have to parse \"${Category.DEFAULT.name}\"")
        return userCategoryMap[categoryName]
            ?: Category.UNRECOGNIZED.also { if (categoryName != Category.UNRECOGNIZED.name) logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    /**
     * [CategoryAmounts]
     */
    @ToJson
    fun toJson(x: CategoryAmounts): String =
        moshi.toJson(x.associate { toJson(it.key) to it.value.toString() })

    @FromJson
    fun fromJson3(s: String): CategoryAmounts {
        return CategoryAmounts(
            moshi.adapter<Map<String, String>>(
                Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            )
                .fromJson(s)!!
                .associate { fromJson1(it.key) to BigDecimal(it.value) }
        )
    }

    /**
     * [CategoryAmountFormulas]
     */
    @ToJson
    fun toJson(x: CategoryAmountFormulas): String =
        moshi.toJson(x.associate { toJson(it.key) to moshi.toJson(it.value) })

    @FromJson
    fun fromJson4(s: String): CategoryAmountFormulas {
        return CategoryAmountFormulas(
            moshi.adapter<Map<String, String>>(
                Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            )
                .fromJson(s)!!
                .associate { fromJson1(it.key) to moshi.fromJson(it.value) }
        )
    }
}