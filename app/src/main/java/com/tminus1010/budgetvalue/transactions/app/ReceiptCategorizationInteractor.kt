package com.tminus1010.budgetvalue.transactions.app

import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptCategorizationInteractor @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor
) {
    // # Model Action
    fun submitPartialCategorization(category: Category) {
        categoryAmounts[category] = categoryAmounts[category]?.let { it + rememberedAmount.value } ?: rememberedAmount.value
        rememberedAmount.easyEmit(BigDecimal("0"))
    }

    fun submitCategorization() {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpendFlow.value!!
                .copy(categoryAmounts = categoryAmounts)
        )
    }

    fun fill(transaction: Transaction) {
        rememberedAmount.easyEmit(CategoryAmounts(categoryAmounts).defaultAmount(transaction.amount))
    }

    val categoryAmounts = SourceHashMap<Category, BigDecimal>()

    // # Model State
    val rememberedAmount = MutableStateFlow(BigDecimal("0"))
    val amountLeftToCategorize =
        combine(
            transactionsInteractor.mostRecentUncategorizedSpendFlow,
            categoryAmounts.flow.map { CategoryAmounts(it) }
        ) { transaction, categoryAmounts ->
            categoryAmounts.defaultAmount(transaction?.amount ?: BigDecimal("0"))
        }
}