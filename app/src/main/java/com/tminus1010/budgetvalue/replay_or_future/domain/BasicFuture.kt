package com.tminus1010.budgetvalue.replay_or_future.domain

import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicFutureDTO
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.app.Transaction

data class BasicFuture(
    override val name: String,
    val searchTexts: List<String>,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val fillCategory: Category,
    override val terminationStatus: TerminationStatus,
) : IFuture {
    override fun predicate(transaction: Transaction): Boolean =
        searchTexts.any { it.uppercase() in transaction.description.uppercase() }

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicFutureDTO(
            name = name,
            searchTexts = searchTexts,
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
            terminationStatus = terminationStatus,
        )

    companion object {
        fun fromDTO(basicFutureDTO: BasicFutureDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoriesInteractor: CategoriesInteractor) = basicFutureDTO.run {
            BasicFuture(
                name = name,
                searchTexts = searchTexts,
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                fillCategory = categoriesInteractor.parseCategory(autoFillCategoryName),
                terminationStatus = terminationStatus,
            )
        }
    }
}