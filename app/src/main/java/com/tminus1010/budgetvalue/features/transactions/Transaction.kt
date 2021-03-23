package com.tminus1010.budgetvalue.features.transactions


import com.tminus1010.budgetvalue.features.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: Map<Category, BigDecimal>,
    val id: String,
) {
    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isSpend get() = amount < BigDecimal.ZERO
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): TransactionDTO {
        return TransactionDTO(
            date,
            description,
            amount,
            categoryAmountsConverter.toString(categoryAmounts),
            id,
        )
    }
    companion object {
        fun fromDTO(transactionDTO: TransactionDTO, categoryAmountsConverter: CategoryAmountsConverter) =
            transactionDTO.run {
                Transaction(
                    date,
                    description,
                    amount,
                    categoryAmountsConverter.toCategoryAmount(categoryAmounts),
                    id,
                )
            }
    }
}