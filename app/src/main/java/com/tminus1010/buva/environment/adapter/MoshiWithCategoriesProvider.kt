package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.environment.database_or_datastore_or_similar.UserCategoriesDAO
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
                    .addLast(KotlinJsonAdapterFactory())
                    .add(CategoryAdapter(it))
                    .add(MiscAdapter)
                    .build()
            }

    /**
     * This synchronous version is a workaround because Room does not expose any way for runtime-mutable adapters.
     */
    val moshi
        get() = Moshi.Builder()
            .add(PairAdapterFactory)
            .add(TripleAdapterFactory)
            .add(BigDecimalAdapter)
            .add(ResetStrategyAdapter)
            .add(ResolutionStrategyAdapter)
            .addLast(KotlinJsonAdapterFactory())
            .add(CategoryAdapter(userCategoryMapProvider.userCategoryMap.value!!))
            .add(MiscAdapter)
            .build()
}