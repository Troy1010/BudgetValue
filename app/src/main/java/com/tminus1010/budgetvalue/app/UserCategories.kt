package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.all_layers.categoryComparator
import com.tminus1010.budgetvalue.data.CategoriesRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCategories @Inject constructor(categoriesRepo: CategoriesRepo) {
    val flow =
        categoriesRepo.userCategories
            .map { it.sortedWith(categoryComparator) }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}