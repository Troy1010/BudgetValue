package com.tminus1010.budgetvalue.model_domain


import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_data.TransactionDTO
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val date: LocalDate,
    val description: String,
    override val amount: BigDecimal,
    override val categoryAmounts: Map<Category, BigDecimal>,
    val id: String,
) : IAmountAndCA {
    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isSpend get() = amount < BigDecimal.ZERO
    override val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun toDTO(typeConverter: TypeConverter): TransactionDTO {
        return TransactionDTO(
            date,
            description,
            amount,
            typeConverter.toString(categoryAmounts),
            id,
        )
    }
    companion object {
        fun fromDTO(transactionDTO: TransactionDTO, typeConverter: TypeConverter) =
            transactionDTO.run {
                Transaction(
                    date,
                    description,
                    amount,
                    typeConverter.toCategoryAmount(categoryAmounts),
                    id,
                )
            }
    }
}