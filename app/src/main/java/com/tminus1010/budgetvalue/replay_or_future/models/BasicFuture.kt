package com.tminus1010.budgetvalue.replay_or_future.models

import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction
import java.util.*

data class BasicFuture(
    override val name: String,
    private val searchText: String,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val autoFillCategory: Category,
    override val isPermanent: Boolean,
) : IFuture {
    override fun predicate(transaction: Transaction): Boolean =
        searchText.uppercase(Locale.ROOT) in transaction.description.uppercase(Locale.ROOT)
    
    override fun categorize(transaction: Transaction): Transaction =
        transaction.categorize(
            CategoryAmountFormulas(categoryAmountFormulas)
                .fillIntoCategory(autoFillCategory, transaction.amount)
                .mapValues { it.value.calcAmount(transaction.amount) }
        )

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        BasicFutureDTO(
            name = name,
            searchText = searchText,
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = autoFillCategory.name,
            isPermanent = isPermanent,
        )

    companion object {
        fun fromDTO(basicFutureDTO: BasicFutureDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoryParser: ICategoryParser) = basicFutureDTO.run {
            BasicFuture(
                name = name,
                searchText = searchText,
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                autoFillCategory = categoryParser.parseCategory(autoFillCategoryName),
                isPermanent = isPermanent
            )
        }
    }
}