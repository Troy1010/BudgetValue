package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.data.CategoryRepo
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