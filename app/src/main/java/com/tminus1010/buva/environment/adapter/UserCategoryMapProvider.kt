package com.tminus1010.buva.environment.adapter

import com.tminus1010.buva.app.UserCategories
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCategoryMapProvider @Inject constructor(
    userCategories: UserCategories,
) {
    /**
     * Because we are using userCategoryMap synchronously in order to avoid using DTOs, any adaptation that depends on it must be redone when this emits.
     */
    val userCategoryMap =
        userCategories.flow
            .map { it.associate { it.name to it } }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    init {
        // TODO: Without this, userCategoryMap.value!! causes NPE. However, using .first() causes a feedback loop.
        runBlocking { userCategoryMap.take(1).collect() }
    }
}