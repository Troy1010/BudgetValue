package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Transaction
import java.io.InputStream

interface ITransactionParser {
    fun parseToTransactions(inputStream: InputStream): List<Transaction>
}