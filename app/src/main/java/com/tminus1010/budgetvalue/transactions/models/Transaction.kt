package com.tminus1010.budgetvalue.transactions.models


import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
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
                    amount.setScale(2),
                    categoryAmountsConverter.toCategoryAmount(categoryAmounts),
                    id,
                )
            }
    }
}