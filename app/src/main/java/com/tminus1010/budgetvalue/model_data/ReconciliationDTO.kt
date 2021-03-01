package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class ReconciliationDTO(
    val localDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)