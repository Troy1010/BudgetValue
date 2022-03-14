package com.tminus1010.budgetvalue.replay_or_future.domain

import androidx.annotation.VisibleForTesting
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue._core.all.extensions.easyEquals
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.math.BigDecimal

@Entity
data class TotalFuture(
    @PrimaryKey
    override val name: String,
    @VisibleForTesting
    val searchTotal: BigDecimal,
    override val categoryAmountFormulas: CategoryAmountFormulas,
    override val fillCategory: Category,
    override val terminationStrategy: TerminationStrategy,
    override val isAutomatic: Boolean,
) : IFuture {
    override fun shouldCategorizeOnImport(transaction: Transaction): Boolean {
        return isAutomatic && searchTotal.easyEquals(transaction.amount)
    }
}