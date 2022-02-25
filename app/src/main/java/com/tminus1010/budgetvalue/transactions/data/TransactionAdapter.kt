package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue._core.all.extensions.ifNull
import com.tminus1010.budgetvalue._core.all.extensions.isPositive
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TransactionAdapter @Inject constructor() {
    fun parseToTransactions(inputStream: InputStream): List<Transaction> {
        return BufferedReader(InputStreamReader(inputStream)).lineSequence()
            .mapNotNull { line -> runCatching { parseToTransaction(line.split(",")) }.getOrElse { logz("Ignoring line:$line\nbecause:", it); null } }
            .toList()
    }

    private val dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyyMMdd")

    /**
     * [row] represents a row from a CSV document.
     */
    private fun parseToTransaction(row: Iterable<String>): Transaction {
        return Transaction(
            date = (row.find { Regex("""^[0-9]{13}${'$'}""").matches(it) } ?: error("Could not find date"))
                .let { LocalDate.parse(it.take(8), dateTimeFormatter1) },
            description = row.maxByOrNull { Regex("""[A-z]""").findAll(it).count() }!!,
            amount = row.find { Regex("""^(-?)([0-9]{0,3},)*[0-9]{1,3}(\.[0-9]*)?${'$'}""").matches(it) }
                .ifNull { row.find { Regex("""^(-?)[0-9]{1,100}(\.[0-9]*)?${'$'}""").matches(it) } }!!
                .toMoneyBigDecimal()
                .let { amount ->
                    // if amount is not negative, then see if any columns denote the fact that this should be a negative value
                    if (amount.isPositive && row.find { Regex("""^Debit${'$'}""").matches(it) } != null)
                        -amount
                    else
                        amount
                },
            categoryAmounts = mapOf(),
            categorizationDate = null,
            id = row.joinToString(","),
        )
    }
}
