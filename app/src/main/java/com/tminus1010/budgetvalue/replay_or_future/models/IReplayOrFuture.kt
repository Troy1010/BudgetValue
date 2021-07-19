package com.tminus1010.budgetvalue.replay_or_future.models

import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction

interface IReplayOrFuture {
    val name: String
    fun predicate(transaction: Transaction): Boolean
    val categoryAmountFormulas: Map<Category, AmountFormula>
    val autoFillCategory: Category
    fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            CategoryAmountFormulas(categoryAmountFormulas)
                .fillIntoCategory(autoFillCategory, transaction.amount)
                .mapValues { it.value.calcAmount(transaction.amount) }
        )
}