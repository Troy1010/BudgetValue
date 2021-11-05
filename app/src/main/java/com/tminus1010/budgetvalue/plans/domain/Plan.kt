package com.tminus1010.budgetvalue.plans.domain

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.domain.LocalDatePeriod
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

@Entity
data class Plan(
    @PrimaryKey
    val localDatePeriod: LocalDatePeriod,
    val amount: BigDecimal,
    val categoryAmounts: CategoryAmounts,
) {
    @Ignore
    val defaultAmount = amount - categoryAmounts.values.sum()
}