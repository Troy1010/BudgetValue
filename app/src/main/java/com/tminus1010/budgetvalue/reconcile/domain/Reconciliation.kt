package com.tminus1010.budgetvalue.reconcile.domain

import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.reconcile.data.model.ReconciliationDTO
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: CategoryAmounts,
    val id: Int = 0
) : CategoryAmountsAndTotal.FromDefaultAmount(categoryAmounts, defaultAmount) {
    constructor(localDate: LocalDate, defaultAmount: BigDecimal, categoryAmounts: Map<Category, BigDecimal>, id: Int = 0) : this(localDate, defaultAmount, CategoryAmounts(categoryAmounts), id)

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): ReconciliationDTO =
        ReconciliationDTO(
            localDate = localDate,
            categoryAmounts = categoryAmountsConverter.toJson(categoryAmounts.filter { it.value.compareTo(BigDecimal.ZERO) != 0 }),
            defaultAmount = defaultAmount,
            id = id,
        )

    companion object {
        fun fromDTO(reconciliationDTO: ReconciliationDTO, categoryAmountsConverter: CategoryAmountsConverter) = reconciliationDTO.run {
            Reconciliation(
                localDate = localDate,
                defaultAmount = defaultAmount,
                categoryAmounts = categoryAmountsConverter.toCategoryAmounts(categoryAmounts),
                id = id,
            )
        }
    }
}