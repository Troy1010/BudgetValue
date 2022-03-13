package com.tminus1010.budgetvalue.replay_or_future.domain

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue._core.all.extensions.easyEquals
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.model.TotalFutureDTO
import com.tminus1010.budgetvalue.transactions.app.Transaction
import java.math.BigDecimal

data class TotalFuture(
    override val name: String,
    @VisibleForTesting
    val searchTotal: BigDecimal,
    override val categoryAmountFormulas: CategoryAmountFormulas,
    override val fillCategory: Category,
    override val terminationStatus: TerminationStatus,
) : IFuture {
    override fun predicate(transaction: Transaction): Boolean =
        searchTotal.easyEquals(transaction.amount)

    fun toDTO(categoryAmountFormulasConverter: CategoryAmountFormulasConverter) =
        TotalFutureDTO(
            name = name,
            searchTotal = searchTotal.toString(),
            categoryAmountFormulasStr = categoryAmountFormulasConverter.toJson(categoryAmountFormulas),
            autoFillCategoryName = fillCategory.name,
            terminationStatus = terminationStatus,
        )

    companion object {
        fun fromDTO(basicFutureDTO: TotalFutureDTO, categoryAmountFormulasConverter: CategoryAmountFormulasConverter, categoriesInteractor: CategoriesInteractor) = basicFutureDTO.run {
            TotalFuture(
                name = name,
                searchTotal = searchTotal.toMoneyBigDecimal(),
                categoryAmountFormulas = CategoryAmountFormulas(categoryAmountFormulasConverter.toCategoryAmountFormulas(categoryAmountFormulasStr)),
                fillCategory = categoriesInteractor.parseCategory(autoFillCategoryName),
                terminationStatus = terminationStatus
            )
        }
    }
}