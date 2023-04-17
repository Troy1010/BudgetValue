package com.tminus1010.buva.environment.android_wrapper

import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Transaction

interface AndroidNavigationWrapper {
    fun navToCreateCategory()
    fun navToEditCategory(category: Category)
    fun navUp()
    suspend fun navToSetString(s: String): String?
    fun navToFutures()
    fun navToTransactions()
    fun navToHistory()
    fun navToCreateFuture()
    suspend fun navToChooseTransaction(): Transaction?
    fun navTo(id: Int)
}