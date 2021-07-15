package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction

data class BasicReplay(
    override val name: String,
    private val description: String,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val autoFillCategory: Category,
) : IReplay {
    override fun predicate(transaction: Transaction): Boolean =
        transaction.description == description

    override fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            categoryAmountFormulas.mapValues { it.value.calcAmount(transaction.amount) }
        )

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicReplayDTO(
            name = name,
            description = description,
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = autoFillCategory.name,
        )

    companion object {
        fun fromDTO(basicReplayDTO: BasicReplayDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoryParser: ICategoryParser) = basicReplayDTO.run {
            BasicReplay(
                name = name,
                description = description,
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                autoFillCategory = categoryParser.parseCategory(autoFillCategoryName),
            )
        }
    }
}