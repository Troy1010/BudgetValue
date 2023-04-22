package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.environment.room.UserCategoriesDAO
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * This moshi can parse [Category], but it transitively depends on [UserCategoriesDAO]
 */
class MoshiWithCategoriesProvider @Inject constructor(
    private val userCategoryMapProvider: UserCategoryMapProvider,
) {
    val moshiFlow =
        userCategoryMapProvider.userCategoryMap
            .map {
                Moshi.Builder()
                    .add(PairAdapterFactory)
                    .add(TripleAdapterFactory)
                    .add(BigDecimalAdapter)
                    .add(ResetStrategyAdapter)
                    .add(ResolutionStrategyAdapter)
                    .add(LocalDateAdapter)
                    .addLast(KotlinJsonAdapterFactory())
                    .add(CategoryAdapter(it))
                    .add(MiscAdapter)
                    .build()
            }
}