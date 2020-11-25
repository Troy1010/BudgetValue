package com.example.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.extensions.toHashMap
import com.example.budgetvalue.layer_ui.misc.sum
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.IParseCategory
import com.example.budgetvalue.model_app.Transaction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class TransactionReceived(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: HashMap<String, BigDecimal> = hashMapOf(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    constructor(transaction: Transaction): this(
        transaction.date,
        transaction.description,
        transaction.amount,
        transaction.categoryAmounts.mapKeys { it.key.name }.toHashMap(),
        transaction.id
    )
    val isUncategorized: Boolean
        get() = categoryAmounts.isNullOrEmpty()
    val isSpend:Boolean
        get() = amount < BigDecimal.ZERO
    val uncategorizedAmount: BigDecimal
        get() {
            return amount - categoryAmounts.values.sum()
        }
    fun toTransaction(parseCategory: IParseCategory): Transaction {
        return Transaction(this, parseCategory)
    }
}