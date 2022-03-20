package com.tminus1010.budgetvalue.reconcile.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.budgetvalue._core.all_layers.extensions.isZero
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
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
            .map { moshiWithCategoriesProvider.moshi.fromJson<CategoryAmounts>(it[key]) }
            .filterNotNull()
            .distinctUntilChanged()
            .stateIn(
                GlobalScope,
                SharingStarted.Eagerly,
                CategoryAmounts()
            )

    suspend fun pushCategoryAmounts(categoryAmounts: CategoryAmounts) {
        dataStore.edit { it[key] = moshiWithCategoriesProvider.moshi.toJson(categoryAmounts) }
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