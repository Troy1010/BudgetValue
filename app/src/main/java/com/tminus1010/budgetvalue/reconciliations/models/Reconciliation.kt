package com.tminus1010.budgetvalue.reconciliations.models

import com.tminus1010.budgetvalue._shared.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) : IHistoryColumnData {
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

    override val title = "Reconciliation"

    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        localDate.toDisplayStr()
}