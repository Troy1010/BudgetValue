package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

/**
 * TransactionParser is able to convert input streams into Transactions.
 * It's unusual that TransactionParser provides the parse methods instead of the
 * read/write methods.. but I do not yet know the best way to get ActivityResults from the repo.
 */
class TransactionParser : ITransactionParser {
    override suspend fun parseToTransactions(inputStream: InputStream) : List<Transaction> = withContext(Dispatchers.IO) {
        val transactions = ArrayList<Transaction>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val row = ArrayList(iterator.next().split(","))
            // find date
            var date: LocalDate? = null
            for ((i, item) in row.withIndex()) {
                if (Regex("""^[0-9]{13}${'$'}""").matches(item)) {
                    date = LocalDate.parse(row[i].substring(0,8), DateTimeFormatter.ofPattern("yyyyMMdd"))
                    row.removeAt(i)
                    break
                }
            }
            if (date == null) {
                continue
            }
            // find amount
            var amount:String? = null
            for ((i, item) in row.withIndex()) {
                // This will not match 1003 b/c it doesn't have a comma. Does that matter..?
                if (Regex("""^(-?)([0-9]{0,3},)*[0-9]{1,3}(\.[0-9]*)?${'$'}""")
                        .matches(item)) {
                    amount = item
                    row.removeAt(i)
                    break
                }
            }
            for ((i, item) in row.withIndex()) {
                if (Regex("""^Debit${'$'}""")
                        .matches(item)) {
                    amount = "-$amount"
                    row.removeAt(i)
                    break
                }
            }
            if (amount == null) {
                continue
            }
            // find description
            var description:String?=null
            val rowCharCount = ArrayList<Int>()
            for (item in row) {
                rowCharCount.add(Regex("""[A-z]""").findAll(item).count())
            }
            val i = rowCharCount.indices.maxBy { rowCharCount[it] }
            i?.apply {
                description = row[i]
                row.removeAt(i)
            }
            if (description == null) {
                continue
            }
            //
            transactions.add(Transaction(date, description!!, amount.toBigDecimal()))
        }
        return@withContext transactions.toList()
    }
}