package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveReconciliationRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
) {
    private val key = stringPreferencesKey("ActiveReconciliationRepo")

    /**
     * CAs are exposed b/c the active reconciliation has no date, and its default amount is a derivative value.
     */
    val activeReconciliationCAs =
        dataStore.data
            .map { moshiWithCategoriesProvider.moshiFlow.first().fromJson<CategoryAmounts>(it[key]) }
            .filterNotNull()
            .stateIn(GlobalScope, SharingStarted.Eagerly, CategoryAmounts()) // TODO: Why not use easyShareIn?

    suspend fun pushCategoryAmounts(categoryAmounts: CategoryAmounts) {
        dataStore.edit { it[key] = moshiWithCategoriesProvider.moshiFlow.first().toJson(categoryAmounts) }
    }

    suspend fun pushCategoryAmount(category: Category, amount: BigDecimal?) {
        pushCategoryAmounts(
            activeReconciliationCAs.first()
                .toMutableMap()
                .also { if (amount == null || amount.isZero) it.remove(category) else it[category] = amount }
                .let { CategoryAmounts(it) }
        )
    }
}