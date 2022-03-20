package com.tminus1010.budgetvalue.replay_or_future.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.math.BigDecimal

@Entity
data class BasicFuture(
    @PrimaryKey
    override val name: String,
    val searchTexts: List<String>,
    override val categoryAmountFormulas: CategoryAmountFormulas,
    override val fillCategory: Category,
    override val terminationStrategy: TerminationStrategy,
    override val isAutomatic: Boolean,
    val totalGuess: BigDecimal,
) : IFuture {
    override fun shouldCategorizeOnImport(transaction: Transaction): Boolean {
        return isAutomatic && searchTexts.any { it.uppercase() in transaction.description.uppercase() }
    }
}