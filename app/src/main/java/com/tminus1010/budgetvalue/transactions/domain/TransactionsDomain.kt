package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.replay.AutoReplayDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.TransactionParser
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.budgetvalue.transactions.models.TransactionsBlock
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionsDomain @Inject constructor(
    private val transactionsRepo: TransactionsRepo,
    private val datePeriodGetter: DatePeriodGetter,
    private val transactionParser: TransactionParser,
    private val autoReplayDomain: AutoReplayDomain
) {
    val transactions = transactionsRepo.transactions
    val transactionBlocks = transactions
        .map(::getBlocksFromTransactions)
    val spends = transactions
        .map { it.filter { it.isSpend } }
    val currentSpendBlockCAs = spends
        .map {
            it
                .filter { it.date in datePeriodGetter.getDatePeriod(LocalDate.now()) }
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
        autoReplayDomain.autoReplays.toSingle().flatMapCompletable { autoReplays ->
            transactionsRepo.tryPush(
                transactionParser.parseToTransactions(inputStream)
                    .map { transaction ->
                        autoReplays[transaction.description]
                            ?.let { transaction.copy(categoryAmounts = it) }
                            ?: transaction
                    }
            )
        }.subscribe()
    }
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<TransactionsBlock> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<TransactionsBlock>()
        if (0 !in transactionsRedefined.indices) return returning
        var datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
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
                    .let { TransactionsBlock(datePeriod, it.first, it.second) }
            if (transactionsRedefined.isEmpty()) break
            datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }
}