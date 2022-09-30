package com.tminus1010.buva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Reconciliation(
    val date: LocalDate,
    override val total: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)