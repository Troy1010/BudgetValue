package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.budgetvalue.model_app.Transaction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class TransactionReceived(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: HashMap<String, BigDecimal> = hashMapOf(),
    @PrimaryKey
    val id: String,
) {
    fun toTransaction(categoryParser: ICategoryParser): Transaction {
        return Transaction(
            date,
            description,
            amount,
            categoryAmounts.mapKeys { categoryParser.parseCategory(it.key) },
            id,
        )
    }
}