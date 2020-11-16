package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.Transaction
import java.io.InputStream

interface ITransactionParser {
    suspend fun parseToTransactions(inputStream: InputStream): List<Transaction>
}