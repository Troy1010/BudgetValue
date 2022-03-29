package com.tminus1010.budgetvalue.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Future(
    @PrimaryKey
    val name: String,
    val categoryAmountFormulas: CategoryAmountFormulas,
    val fillCategory: Category,
    val terminationStrategy: TerminationStrategy,
    val terminationDate: LocalDate?,
    val isAvailableForManual: Boolean,
    val onImportMatcher: TransactionMatcher?,
    val totalGuess: BigDecimal,
) {
    fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            categoryAmountFormulas
                .fillIntoCategory(fillCategory, transaction.amount)
                .toCategoryAmounts(transaction.amount)
        )
}