package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.Transaction
import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<Transaction>
}