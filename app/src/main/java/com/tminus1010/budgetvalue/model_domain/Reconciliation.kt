package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.layer_domain.CategoryAmountsConverter
import com.tminus1010.budgetvalue.model_data.ReconciliationDTO
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) : IAmountAndCA {
    override val amount get() = defaultAmount + categoryAmounts.values.sum()
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