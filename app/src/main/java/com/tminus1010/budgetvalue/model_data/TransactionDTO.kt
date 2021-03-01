package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_domain.Transaction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class TransactionDTO(
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
            typeConverter.toCategoryAmount(categoryAmounts),
            id,
        )
    }
}