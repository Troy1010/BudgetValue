package com.tminus1010.budgetvalue.modules.transactions

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
    @PrimaryKey
    val id: String,
)