package com.tminus1010.budgetvalue.transactions.app

import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptCategorizationInteractor @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor
) {
    // # UserIntents
    fun userSubmitPartialCategorization() {
        categoryAmounts[currentCategory.value!!] = categoryAmounts[currentCategory.value!!]?.let { it + currentChosenAmount.value } ?: currentChosenAmount.value
        currentChosenAmount.easyEmit(BigDecimal("0"))
        currentCategory.easyEmit(null)
    }

    fun userSubmitCategorization() {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend.value!!.first!!
                .copy(categoryAmounts = categoryAmounts)
        )
    }

    // # Model State
    val currentChosenAmount = MutableStateFlow(BigDecimal("0"))
    val currentCategory = MutableStateFlow<Category?>(null)

    // # Model Action
    fun fill(transaction: Transaction) {
        currentChosenAmount.easyEmit(CategoryAmounts(categoryAmounts).defaultAmount(transaction.amount))
    }

    // # Internal
    private val categoryAmounts = mutableMapOf<Category, BigDecimal>()
}