package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import java.math.BigDecimal

data class BasicReplay(
    override val name: String,
    private val description: String,
    private val categoryAmounts: Map<Category, BigDecimal>,
    override val isAutoReplay: Boolean,
) : IReplay {
    override fun predicate(transaction: Transaction): Boolean =
        transaction.description == description

    override fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(categoryAmounts)

    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter) =
        BasicReplayDTO(
            name = name,
            description = description,
            categoryAmounts = categoryAmountsConverter.toJson(categoryAmounts),
            isAutoReplay = isAutoReplay
        )

    companion object {
        fun fromDTO(basicReplayDTO: BasicReplayDTO, categoryAmountsConverter: CategoryAmountsConverter) = basicReplayDTO.run {
            BasicReplay(
                name = name,
                description = description,
                categoryAmounts = categoryAmountsConverter.toCategoryAmounts(categoryAmounts),
                isAutoReplay = isAutoReplay
            )
        }
    }
}