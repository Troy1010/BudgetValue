package com.tminus1010.budgetvalue.plans.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class PlanDTO(
    @PrimaryKey
    val startDate: LocalDate,
    val endDate: LocalDate,
    val amount: BigDecimal,
    val categoryAmounts: String?,
)
