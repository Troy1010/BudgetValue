package com.tminus1010.budgetvalue.model_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class ReconcileCategoryAmount(
    @PrimaryKey
    override val categoryName: String,
    override val amount: BigDecimal = BigDecimal.ZERO
): ICategoryAmountReceived