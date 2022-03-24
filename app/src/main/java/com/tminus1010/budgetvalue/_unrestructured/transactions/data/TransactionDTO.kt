package com.tminus1010.budgetvalue._unrestructured.transactions.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class TransactionDTO(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: String?,
    val categorizationDate: LocalDate?,
    @PrimaryKey
    val id: String,
)