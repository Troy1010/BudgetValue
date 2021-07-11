package com.tminus1010.budgetvalue.transactions.models


import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class Transaction(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val categorizationDate: LocalDate?,
    val id: String,
) {
    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isCategorized get() = !isUncategorized
    val isSpend get() = amount < BigDecimal.ZERO
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun categorize(categoryAmounts: Map<Category, BigDecimal>): Transaction {
        return this.copy(
            categoryAmounts = categoryAmounts,
            categorizationDate = LocalDate.now()
        )
    }

    fun categorize(category: Category): Transaction {
        return categorize(
            categoryAmounts
                .filter { it.key != category }
                .let { categoryAmounts ->
                    categoryAmounts
                        .copy(category to amount - categoryAmounts.values.sum())
                }
        )
    }

    fun calcCAsAdjustedForNewTotal(newTotal: BigDecimal): Map<Category, BigDecimal> {
        val dumpEverythingIntoLast = (defaultAmount.compareTo(BigDecimal.ZERO) == 0)
        var totalSoFar = BigDecimal.ZERO
        return (newTotal / amount)
            .let { ratio ->
                categoryAmounts.mapValues {
                    if (dumpEverythingIntoLast && it.key == categoryAmounts.keys.lastOrNull())
                        newTotal - totalSoFar
                    else
                        (it.value * ratio).setScale(2, RoundingMode.HALF_UP)
                            .also { totalSoFar += it }
                }
            }
    }

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): TransactionDTO {
        return TransactionDTO(
            date,
            description,
            amount,
            categoryAmountsConverter.toJson(categoryAmounts.filter { it.value.compareTo(BigDecimal.ZERO) != 0 }),
            categorizationDate,
            id,
        )
    }

    companion object {
        fun fromDTO(transactionDTO: TransactionDTO, categoryAmountsConverter: CategoryAmountsConverter) = transactionDTO.run {
            Transaction(
                date,
                description,
                amount.setScale(2),
                categoryAmountsConverter.toCategoryAmounts(categoryAmounts),
                categorizationDate,
                id,
            )
        }
    }
}