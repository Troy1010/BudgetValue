package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.transactions.models.TransactionsBlock
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Observable
import java.io.InputStream
import java.math.BigDecimal

interface ITransactionsDomain {
    val transactions: Observable<List<Transaction>>
    val transactionBlocks: Observable<List<TransactionsBlock>>
    val spends: Observable<List<Transaction>>
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>>
    val uncategorizedSpends: Observable<List<Transaction>>
    val uncategorizedSpendsSize: Observable<String>
    fun importTransactions(inputStream: InputStream)
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<TransactionsBlock>
}