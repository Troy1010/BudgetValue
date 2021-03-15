package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.CategoryAmountsConverter
import com.tminus1010.budgetvalue.model_data.ReconciliationDTO
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    val defaultAmount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) {
    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): ReconciliationDTO {
        return ReconciliationDTO(
            localDate = localDate,
            categoryAmounts = categoryAmountsConverter.toString(categoryAmounts),
            amount = defaultAmount,
            id = id,)
    }

    companion object {
        fun fromDTO(reconciliationDTO: ReconciliationDTO, categoryAmountsConverter: CategoryAmountsConverter) =
            reconciliationDTO.run {
                Reconciliation(localDate,
                    amount,
                    categoryAmountsConverter.toCategoryAmount(categoryAmounts),
                    id)
            }
    }
}