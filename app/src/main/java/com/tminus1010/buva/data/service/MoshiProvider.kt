package com.tminus1010.buva.data.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.buva.domain.Category
import dagger.Reusable
import javax.inject.Inject

/**
 * This moshi provider is global, but it cannot parse [Category], b/c doing so depends on a DAO.
 *
 * If you need to do so, use [MoshiWithCategoriesProvider].
 */
@Reusable
class MoshiProvider @Inject constructor() {
    val moshi =
        Moshi.Builder()
            .add(MoshiAdapters)
            .addLast(KotlinJsonAdapterFactory())
            .build()
}