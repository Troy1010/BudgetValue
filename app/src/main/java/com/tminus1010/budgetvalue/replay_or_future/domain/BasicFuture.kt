package com.tminus1010.budgetvalue.replay_or_future.domain

import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicFutureDTO
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.util.*

data class BasicFuture(
    override val name: String,
    val searchText: String,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val fillCategory: Category,
    override val terminationStatus: TerminationStatus,
) : IFuture {
    override fun predicate(transaction: Transaction): Boolean =
        searchText.uppercase(Locale.ROOT) in transaction.description.uppercase(Locale.ROOT)

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicFutureDTO(
            name = name,
            searchText = searchText,
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
            terminationStatus = terminationStatus,
        )

    companion object {
        fun fromDTO(basicFutureDTO: BasicFutureDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoriesInteractor: CategoriesInteractor) = basicFutureDTO.run {
            BasicFuture(
                name = name,
                searchText = searchText,
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                fillCategory = categoriesInteractor.parseCategory(autoFillCategoryName),
                terminationStatus = terminationStatus
            )
        }
    }
}