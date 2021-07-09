package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.transactions.models.Transaction

interface IReplay {
    val name: String
    fun predicate(transaction: Transaction): Boolean
    fun categorize(transaction: Transaction): Transaction
}