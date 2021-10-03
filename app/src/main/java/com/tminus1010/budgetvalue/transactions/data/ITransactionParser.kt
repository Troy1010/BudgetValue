package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue.transactions.models.Transaction
import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<Transaction>
}