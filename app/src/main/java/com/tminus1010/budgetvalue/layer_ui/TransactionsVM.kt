package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Block
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream
import java.math.BigDecimal

class TransactionsVM(private val repo: Repo, val datePeriodGetter: DatePeriodGetter):ViewModel() {
    val transactions = repo.transactions
    val transactionBlocks = transactions
        .map(::getBlocksFromTransactions)
    val spends = transactions
        .map { it.filter { it.isSpend } }
    val uncategorizedSpends = spends
        .map { it.filter { it.isUncategorized } }
    val uncategorizedSpendsSize = uncategorizedSpends
        .map { it.size.toString() }
    fun importTransactions(inputStream: InputStream) {
        repo.tryAdd(repo.parseToTransactions(inputStream))
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<Block> {
        val transactionsRedefined = transactions.sortedBy { it.date }.toMutableList()
        val returning = ArrayList<Block>()
        if (0 !in transactionsRedefined.indices) return returning
        var datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
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
            datePeriod = datePeriodGetter.getDatePeriod(transactionsRedefined[0].date)
        }
        return returning
    }
}