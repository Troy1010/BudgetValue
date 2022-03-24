package com.tminus1010.budgetvalue.domain.plan

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.CategoryAmountsAndTotal
import java.math.BigDecimal

@Entity
data class Plan(
    @PrimaryKey
    val localDatePeriod: LocalDatePeriod,
    override val total: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total)