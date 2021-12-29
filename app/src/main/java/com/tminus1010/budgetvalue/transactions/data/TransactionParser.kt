package com.tminus1010.budgetvalue.transactions.data

import com.tminus1010.budgetvalue._core.all.extensions.isPositive
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TransactionParser @Inject constructor(
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    fun parseToTransactions(inputStream: InputStream): List<Transaction> {
        val transactions = ArrayList<TransactionDTO>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val entireString = iterator.next()
            val row = ArrayList(entireString.split(","))
            // find date
            var date: LocalDate? = null
            for ((i, item) in row.withIndex())
                if (Regex("""^[0-9]{13}${'$'}""").matches(item)) {
                    date = LocalDate.parse(
                        row[i].substring(0, 8),
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                    )
                    row.removeAt(i)
                    break
                }
            if (date == null) continue
            // # Determine amount
            var amount: String? = null
            // from left to right, see if any are valid amounts
            for ((i, s) in row.withIndex())
                if (s.matches(Regex("""^(-?)([0-9]{0,3},)*[0-9]{1,3}(\.[0-9]*)?${'$'}"""))) {
                    amount = s
                    row.removeAt(i)
                    break
                }
            if (amount == null) continue
            // if amount is not negative, then from left to right, see if any denote the fact that this should be a negative value
            if (amount.toBigDecimal().isPositive)
                for ((i, s) in row.withIndex())
                    if (s.matches(Regex("""^Debit${'$'}"""))) {
                        amount = "-$amount"
                        row.removeAt(i)
                        break
                    }
            if (amount == null) continue
            // find description
            var description: String? = null
            val rowCharCount = ArrayList<Int>()
            for (item in row)
                rowCharCount.add(Regex("""[A-z]""").findAll(item).count())
            val i = rowCharCount.indices.maxByOrNull { rowCharCount[it] }
            i?.apply {
                description = row[i]
                row.removeAt(i)
            }
            if (description == null) continue
            //
            transactions.add(TransactionDTO(date, description!!, amount.toBigDecimal(), null, null, entireString))
        }
        return transactions.map { Transaction.fromDTO(it, categoryAmountsConverter) }.toList()
    }
}