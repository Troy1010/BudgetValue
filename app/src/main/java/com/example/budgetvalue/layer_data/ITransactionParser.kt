package com.example.budgetvalue.layer_data

import com.example.budgetvalue.models.Transaction
import java.io.InputStream

interface ITransactionParser {
    suspend fun parseInputStreamToTransactions(inputStream: InputStream): List<Transaction>
}