package com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain

import com.tminus1010.budgetvalue.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction

interface IReplayOrFuture {
    val name: String
    fun shouldCategorizeOnImport(transaction: Transaction): Boolean
    val categoryAmountFormulas: CategoryAmountFormulas
    val fillCategory: Category
    fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            categoryAmountFormulas
                .fillIntoCategory(fillCategory, transaction.amount)
                .mapValues { it.value.calcAmount(transaction.amount) }
        )
}