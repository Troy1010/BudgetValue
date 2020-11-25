package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.TransactionReceived
import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<TransactionReceived>
}