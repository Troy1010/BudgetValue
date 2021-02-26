package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.TransactionReceived
import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<TransactionReceived>
}