package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction

interface IReplayOrFuture {
    val name: String
    fun predicate(transaction: Transaction): Boolean
    fun categorize(transaction: Transaction): Transaction
    val categoryAmountFormulas: Map<Category, AmountFormula>
    val autoFillCategory: Category
}