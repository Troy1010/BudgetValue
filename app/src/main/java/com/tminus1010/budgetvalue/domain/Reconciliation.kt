package com.tminus1010.budgetvalue.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Reconciliation(
    val localDate: LocalDate,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : CategoryAmountsAndTotal.FromDefaultAmount(categoryAmounts, defaultAmount) {
    constructor(localDate: LocalDate, defaultAmount: BigDecimal, categoryAmounts: Map<Category, BigDecimal>, id: Int = 0) : this(localDate, defaultAmount, CategoryAmounts(categoryAmounts), id)
}