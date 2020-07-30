package com.example.budgetvalue.layers.data_layer

import android.net.Uri
import com.example.budgetvalue.models.Transaction

class TransactionParser : ITransactionParser {
    override fun parseCsvToTransactions(uri: Uri): List<Transaction> {
        return emptyList()
    }
}