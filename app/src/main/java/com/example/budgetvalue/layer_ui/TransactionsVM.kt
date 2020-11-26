package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Block
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.LocalDatePeriod
import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream
import java.math.BigDecimal
import java.time.Period

class TransactionsVM(private val repo: Repo):ViewModel() {
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
        repo.clearTransactions()
            .andThen(repo.add(repo.parseToTransactions(inputStream)))
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
    fun getBlocksFromTransactions(transactions: List<Transaction>): List<Block> {
        val transactionsRedefined = transactions.sortedBy { it.date }
        val returning = ArrayList<Block>()
        var datePeriod = LocalDatePeriod(transactionsRedefined[0].date, Period.ofDays(13))
        while (datePeriod.startDate <= transactionsRedefined.last().date) {
            val transactionSet = transactions
                .filter { it.date in datePeriod }
            if (transactionSet.isNotEmpty())
                returning += transactionSet
                    .fold(Pair(BigDecimal.ZERO, hashMapOf<Category, BigDecimal>())) { acc, transaction ->
                        transaction.categoryAmounts.forEach { acc.second[it.key] = it.value + (acc.second[it.key]?:BigDecimal.ZERO) }
                        Pair(acc.first+transaction.amount, acc.second )
                    }
                    .let { Block(datePeriod, it.first, it.second) }
            datePeriod = LocalDatePeriod(datePeriod.endDate.plusDays(1), Period.ofDays(14))
        }
        return returning
    }
}