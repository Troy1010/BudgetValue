package com.tminus1010.budgetvalue.features.transactions

import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<Transaction>
}