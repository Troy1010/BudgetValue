package com.tminus1010.buva.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
@Entity
data class Future(
    @PrimaryKey
    val name: String,
    val categoryAmountFormulas: CategoryAmountFormulas,
    val fillCategory: Category,
    val terminationStrategy: TerminationStrategy,
    val terminationDate: LocalDate?,
    val isAvailableForManual: Boolean,
    val onImportTransactionMatcher: TransactionMatcher?,
    val totalGuess: BigDecimal,
) : ICategorizer, Parcelable {
    override fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            categoryAmountFormulas
                .fillIntoCategory(fillCategory, transaction.amount)
                .toCategoryAmounts(transaction.amount)
        )
}