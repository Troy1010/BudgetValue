package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import java.math.BigDecimal

data class Replay(
    override val name: String,
    private val description: String,
    private val categoryAmounts: Map<Category, BigDecimal>,
) : IReplay {
    override val key: String
        get() = description

    override fun toKey(transaction: Transaction): String =
        transaction.description

    override val categorize: (transaction: Transaction) -> Transaction = { transaction ->
        transaction.categorize(categoryAmounts)
    }

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter) =
        ReplayDTO(
            name = name,
            description = description,
            categoryAmounts = categoryAmountsConverter.toJson(categoryAmounts)
        )

    companion object {
        fun fromDTO(replayDTO: ReplayDTO, categoryAmountsConverter: CategoryAmountsConverter) = replayDTO.run {
            Replay(
                name = name,
                description = description,
                categoryAmounts = categoryAmountsConverter.toCategoryAmounts(categoryAmounts)
            )
        }
    }
}