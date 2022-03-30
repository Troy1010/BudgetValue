package com.tminus1010.budgetvalue.domain

interface ICategorizer {
    fun categorize(transaction: Transaction): Transaction
}