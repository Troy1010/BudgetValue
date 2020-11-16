package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.Transaction
import java.io.InputStream

interface ITransactionParser {
    suspend fun parseInputStreamToTransactions(inputStream: InputStream): List<Transaction>
}