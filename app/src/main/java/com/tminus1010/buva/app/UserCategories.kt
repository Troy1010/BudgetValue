package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCategories @Inject constructor(categoryRepo: CategoryRepo) {
    val flow =
        categoryRepo.userCategories
            .map { it.sortedWith(categoryComparator) }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}

val UserCategories.firstUnlimited
    get() = flow.map { categories -> categories.firstOrNull { it.reconciliationStrategyGroup is ReconciliationStrategyGroup.Unlimited } }