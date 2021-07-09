package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.transactions.models.Transaction

interface IReplay {
    val name: String
    val key: String
    fun toKey(transaction: Transaction): String
    val categorize: (transaction: Transaction) -> Transaction
}