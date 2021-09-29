package com.tminus1010.budgetvalue.reconciliations.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) {
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
                id = id
            )
        }
    }
}