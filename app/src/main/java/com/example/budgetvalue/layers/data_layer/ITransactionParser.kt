package com.example.budgetvalue.layers.data_layer

import android.net.Uri
import com.example.budgetvalue.models.Transaction
import java.io.InputStream

interface ITransactionParser {
    fun parseInputStreamToTransactions(inputStream: InputStream): List<Transaction>
}