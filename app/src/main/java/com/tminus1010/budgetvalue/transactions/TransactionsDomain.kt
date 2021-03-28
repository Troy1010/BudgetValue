package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import dagger.Reusable
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@Reusable
class TransactionsDomain @Inject constructor(
    private val domainFacade: DomainFacade,
) : ITransactionsDomain {
    override val transactions = domainFacade.transactions
    override val transactionBlocks = transactions
        .map(::getBlocksFromTransactions)
    override val spends = transactions
        .map { it.filter { it.isSpend } }
    override val currentSpendBlockCAs = spends
        .map {
            it
                .filter { it.date in domainFacade.getDatePeriod(LocalDate.now()) }
                .map { it.categoryAmounts }
                .fold(mapOf<Category, BigDecimal>()) { acc, v ->
                    mutableSetOf<Category>().apply { addAll(acc.keys); addAll(v.keys) }
                        .associateWith { (acc[it] ?: BigDecimal.ZERO) + (v[it] ?: BigDecimal.ZERO) }
                }
        }
    override val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    override val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    override fun importTransactions(inputStream: InputStream) {
        domainFacade.tryPush(domainFacade.parseToTransactions(inputStream)).launch()
    }
    override fun getBlocksFromTransactions(transactions: List<Transaction>): List<Block> {
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
                        transaction.categoryAmounts.forEach { acc.second[it.key] = it.value + (acc.second[it.key]?: BigDecimal.ZERO) }
                        Pair(acc.first+transaction.amount, acc.second )
                    }
                    .let { Block(datePeriod, it.first, it.second) }
            if (transactionsRedefined.isEmpty()) break
            datePeriod = domainFacade.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }
}