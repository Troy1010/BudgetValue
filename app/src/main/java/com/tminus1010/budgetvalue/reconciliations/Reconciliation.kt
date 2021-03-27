package com.tminus1010.budgetvalue.reconciliations

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.history.IHistoryColumnData
import com.tminus1010.tmcommonkotlin.misc.extensions.toDisplayStr
import java.math.BigDecimal
import java.time.LocalDate

data class Reconciliation(
    val localDate: LocalDate,
    override val defaultAmount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
    val id: Int = 0
) : IHistoryColumnData {
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

    override val title = "Reconciliation"

    override fun subTitle(datePeriodGetter: IDatePeriodGetter): String? =
        localDate.toDisplayStr()
}