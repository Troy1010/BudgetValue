package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.categories.models.Category
import javax.inject.Inject

/**
 * This moshi can parse [Category], but it transitively depends on [UserCategoriesDAO2]
 */
class MoshiWithCategoriesProvider @Inject constructor(moshiWithCategoriesAdapters: MoshiWithCategoriesAdapters) {
    val moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(moshiWithCategoriesAdapters)
            .add(MoshiAdapters)
            .build()
}