package com.tminus1010.budgetvalue.data.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.domain.Category

/**
 * This moshi provider is global, but it cannot parse [Category], b/c doing so depends on a DAO.
 *
 * If you need to do so, use [MoshiWithCategoriesProvider].
 */
object MoshiProvider {
    val moshi =
        Moshi.Builder()
            .add(MoshiAdapters)
            .addLast(KotlinJsonAdapterFactory())
            .build()
}