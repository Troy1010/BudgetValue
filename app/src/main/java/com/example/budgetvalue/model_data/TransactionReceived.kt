package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.collections.HashMap

@Entity
data class TransactionReceived(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: HashMap<String, BigDecimal> = hashMapOf(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    val isUncategorized: Boolean
        get() = categoryAmounts.isNullOrEmpty()
    val isSpend:Boolean
        get() = amount < BigDecimal.ZERO
    val uncategorizedAmount: BigDecimal
        get() {
            return amount - categoryAmounts.values.sum()
        }
    fun toTransaction(transformCategoryAction: (String) -> Category): Transaction {
        return Transaction(this, transformCategoryAction)
    }
}