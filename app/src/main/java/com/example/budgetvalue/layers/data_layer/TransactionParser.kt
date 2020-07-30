package com.example.budgetvalue.layers.data_layer

import android.net.Uri
import com.example.budgetvalue.models.Transaction
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class TransactionParser : ITransactionParser {
    override fun parseInputStreamToTransactions(inputStream: InputStream) : List<Transaction> {
        val transactions = ArrayList<Transaction>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val row = ArrayList(iterator.next().split(","))
            // find date
            var date: String? = null
            for ((i, item) in row.withIndex()) {
                if (Regex("""^[0-9]{13}${'$'}""").matches(item)) {
                    date = row[i]
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
            transactions.add(Transaction(date, description!!, amount))
        }
        return transactions.toList()
    }
}