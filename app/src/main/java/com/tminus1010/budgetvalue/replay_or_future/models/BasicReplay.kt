package com.tminus1010.budgetvalue.replay_or_future.models

import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.app.Transaction

data class BasicReplay(
    override val name: String,
    val searchTexts: List<String>,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val fillCategory: Category,
) : IReplay {
    override fun predicate(transaction: Transaction): Boolean =
        searchTexts.any { it.uppercase() in transaction.description.uppercase() }

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicReplayDTO(
            name = name,
            searchTextsStr = searchTexts.joinToString("`"),
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
        )

    companion object {
        fun fromDTO(basicReplayDTO: BasicReplayDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoryParser: ICategoryParser) = basicReplayDTO.run {
            BasicReplay(
                name = name,
                searchTexts = searchTextsStr.split("`"),
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                fillCategory = categoryParser.parseCategory(autoFillCategoryName),
            )
        }
    }
}