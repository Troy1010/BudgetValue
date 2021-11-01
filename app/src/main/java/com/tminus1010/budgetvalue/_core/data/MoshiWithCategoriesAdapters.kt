package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue._core.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import javax.inject.Inject


class MoshiWithCategoriesAdapters @Inject constructor(
    private val categoriesInteractor: CategoriesInteractor
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
     * Map<[Category], [BigDecimal]>
     */
    val adapter1 =
        moshi.adapter<Map<String, String>>(
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )

    @ToJson
    fun toJson(x: Map<@JvmSuppressWildcards Category, @JvmSuppressWildcards BigDecimal>): String {
        return adapter1.toJson(x.associate { toJson(it.key) to it.value.toString() })
    }

    @FromJson
    fun fromJson8(s: String): Map<@JvmSuppressWildcards Category, @JvmSuppressWildcards BigDecimal> {
        return adapter1.fromJson(s)!!
            .associate { categoriesInteractor.parseCategory(it.key) to BigDecimal(it.value) }
    }

    /**
     * [CategoryAmounts]
     */
    @ToJson
    fun toJson(x: CategoryAmounts): String =
        moshi.toJson(x.associate { toJson(it.key) to it.value.toString() })

    @FromJson
    fun fromJson3(s: String): CategoryAmounts {
        return moshi.adapter<Map<String, String>>(
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )
            .fromJson(s)!!
            .associate { fromJson1(it.key) to BigDecimal(it.value) }
            .let { CategoryAmounts(it) }
    }
}