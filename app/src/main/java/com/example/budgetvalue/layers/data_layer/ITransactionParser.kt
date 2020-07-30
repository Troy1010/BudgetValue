package com.example.budgetvalue.layers.data_layer

import android.net.Uri
import com.example.budgetvalue.models.Transaction

interface ITransactionParser {
    fun parseCsvToTransactions(uri: Uri): List<Transaction>
}