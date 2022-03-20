package com.tminus1010.budgetvalue.all_features.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.tminus1010.budgetvalue.all_features.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import javax.inject.Inject


class MoshiWithCategoriesAdapters @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor,
) {
    /**
     * [Category]
     */
    @ToJson
    fun toJson(x: Category): String =
        x.name

    @FromJson
    fun fromJson1(s: String): Category =
        categoriesInteractor.parseCategory(s)

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