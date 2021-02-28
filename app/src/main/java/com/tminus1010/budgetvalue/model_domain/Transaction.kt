package com.tminus1010.budgetvalue.model_domain


import com.tminus1010.budgetvalue.layer_domain.TypeConverter
import com.tminus1010.budgetvalue.model_data.TransactionReceived
import com.tminus1010.tmcommonkotlin.rx.extensions.sum
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
    fun toTransactionReceived(typeConverter: TypeConverter): TransactionReceived {
        return TransactionReceived(
            date,
            description,
            amount,
            typeConverter.toString(categoryAmounts),
            id,
        )
    }
}