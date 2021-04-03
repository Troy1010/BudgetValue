package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate

class TransactionsVM(
    private val domainFacade: DomainFacade,
) : ViewModel() {
    val transactions = domainFacade.transactions
    val transactionBlocks = transactions
        .map(::getBlocksFromTransactions)
    val spends = transactions
        .map { it.filter { it.isSpend } }
    val currentSpendBlockCAs = spends
        .map {
            it
                .filter { it.date in domainFacade.getDatePeriod(LocalDate.now()) }
                .map { it.categoryAmounts }
                .fold(mapOf<Category, BigDecimal>()) { acc, v ->
                    mutableSetOf<Category>().apply { addAll(acc.keys); addAll(v.keys) }
                        .associateWith { (acc[it] ?: BigDecimal.ZERO) + (v[it] ?: BigDecimal.ZERO) }
                }
        }
    val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    fun importTransactions(inputStream: InputStream) {
        domainFacade.tryPush(domainFacade.parseToTransactions(inputStream)).launch()
    }
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<Block> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<Block>()
        if (0 !in transactionsRedefined.indices) return returning
        var datePeriod = domainFacade.getDatePeriod(transactionsRedefined[0].date)
        while (datePeriod.startDate <= transactionsRedefined.last().date) {
            val transactionSet = transactionsRedefined
                .filter { it.date in datePeriod }
            transactionsRedefined.removeIf { it.date in datePeriod }
            if (transactionSet.isNotEmpty())
                returning += transactionSet
                    .fold(Pair(BigDecimal.ZERO, hashMapOf<Category, BigDecimal>())) { acc, transaction ->
                        transaction.categoryAmounts.forEach { acc.second[it.key] = it.value + (acc.second[it.key]?:BigDecimal.ZERO) }
                        Pair(acc.first+transaction.amount, acc.second )
                    }
                    .let { Block(datePeriod, it.first, it.second) }
            if (transactionsRedefined.isEmpty()) break
            datePeriod = domainFacade.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }
}