package com.tminus1010.buva.domain

interface ICategorizer {
    fun categorize(transaction: Transaction): Transaction
}