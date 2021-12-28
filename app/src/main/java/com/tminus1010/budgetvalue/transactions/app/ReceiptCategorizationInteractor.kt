package com.tminus1010.budgetvalue.transactions.app

import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptCategorizationInteractor @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor
) {
    // # Model Action
    fun submitPartialCategorization() {
        categoryAmounts[currentCategory.value!!] = categoryAmounts[currentCategory.value!!]?.let { it + currentChosenAmount.value } ?: currentChosenAmount.value
        currentChosenAmount.easyEmit(BigDecimal("0"))
        currentCategory.easyEmit(null)
    }

    fun submitCategorization() {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend.value!!.first!!
                .copy(categoryAmounts = categoryAmounts)
        )
    }

    fun fill(transaction: Transaction) {
        currentChosenAmount.easyEmit(CategoryAmounts(categoryAmounts).defaultAmount(transaction.amount))
    }

    val categoryAmounts = SourceHashMap<Category, BigDecimal>()

    // # Model State
    val categoryAmountsFlow = categoryAmounts.observable.asFlow()
    val currentChosenAmount = MutableStateFlow(BigDecimal("0"))
    val currentCategory = MutableStateFlow<Category?>(null)
    val amountLeftToCategorize =
        combine(
            transactionsInteractor.mostRecentUncategorizedSpend.asFlow().map { it.first },
            categoryAmountsFlow.map { CategoryAmounts(it) }
        ) { transaction, categoryAmounts ->
            categoryAmounts.defaultAmount(transaction?.amount ?: BigDecimal("0"))
        }
}