package com.tminus1010.buva.environment

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.buva.domain.Category
import javax.inject.Inject

/**
 * This moshi can parse [Category], but it transitively depends on [UserCategoriesDAO]
 */
class MoshiWithCategoriesProvider @Inject constructor(moshiWithCategoriesAdapters: MoshiWithCategoriesAdapters) {
    val moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(moshiWithCategoriesAdapters)
            .add(MoshiAdapters)
            .build()
}