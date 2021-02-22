package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.layer_data.TypeConverter
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.budgetvalue.model_app.Transaction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class TransactionReceived(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: String?,
    @PrimaryKey
    val id: String,
) {
    fun toTransaction(typeConverter: TypeConverter): Transaction {
        return Transaction(
            date,
            description,
            amount,
            typeConverter.categoryAmounts(categoryAmounts),
            id,
        )
    }
}