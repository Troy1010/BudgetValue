package com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain

import com.tminus1010.budgetvalue.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue._unrestructured.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction

data class BasicReplay(
    override val name: String,
    val searchTexts: List<String>,
    override val categoryAmountFormulas: CategoryAmountFormulas,
    override val fillCategory: Category,
) : IReplay {
    override fun shouldCategorizeOnImport(transaction: Transaction): Boolean =
        searchTexts.any { it.uppercase() in transaction.description.uppercase() }

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicReplayDTO(
            name = name,
            searchTextsStr = searchTexts.joinToString("`"),
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
        )

    companion object {
        fun fromDTO(basicReplayDTO: BasicReplayDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoriesInteractor: CategoriesInteractor) = basicReplayDTO.run {
            BasicReplay(
                name = name,
                searchTexts = searchTextsStr.split("`"),
                categoryAmountFormulas = CategoryAmountFormulas(categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr)),
                fillCategory = categoriesInteractor.parseCategory(autoFillCategoryName),
            )
        }
    }
}