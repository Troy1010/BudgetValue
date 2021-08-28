package com.tminus1010.budgetvalue.replay_or_future.models

import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction
import java.math.BigDecimal

data class TotalFuture(
    override val name: String,
    private val searchTotal: BigDecimal,
    override val categoryAmountFormulas: Map<Category, AmountFormula>,
    override val fillCategory: Category,
    override val isPermanent: Boolean,
) : IFuture {
    override fun predicate(transaction: Transaction): Boolean =
        searchTotal.compareTo(transaction.amount) != 0

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        TotalFutureDTO(
            name = name,
            searchTotal = searchTotal.toString(),
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
            isPermanent = isPermanent,
        )

    companion object {
        fun fromDTO(basicFutureDTO: TotalFutureDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoryParser: ICategoryParser) = basicFutureDTO.run {
            TotalFuture(
                name = name,
                searchTotal = searchTotal.toMoneyBigDecimal(),
                categoryAmountFormulas = categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr),
                fillCategory = categoryParser.parseCategory(autoFillCategoryName),
                isPermanent = isPermanent
            )
        }
    }
}