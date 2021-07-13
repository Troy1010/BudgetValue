package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction

interface IReplayOrFuture {
    val name: String
    fun predicate(transaction: Transaction): Boolean
    fun categorize(transaction: Transaction): Transaction
    val autoFillCategory: Category
}