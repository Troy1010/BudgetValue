package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.core.Observable
import java.io.InputStream
import java.math.BigDecimal

interface ITransactionsDomain {
    val transactions: Observable<List<Transaction>>
    val transactionBlocks: Observable<List<Block>>
    val spends: Observable<List<Transaction>>
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>>
    val uncategorizedSpends: Observable<List<Transaction>>
    val uncategorizedSpendsSize: Observable<String>
    fun importTransactions(inputStream: InputStream)
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<Block>
}