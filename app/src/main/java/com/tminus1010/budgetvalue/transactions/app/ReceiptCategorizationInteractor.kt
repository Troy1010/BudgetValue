package com.tminus1010.budgetvalue.transactions.app

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_features.domain.CategoryAmounts
import com.tminus1010.budgetvalue.all_features.framework.source_objects.SourceList
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
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
        categoryAmounts.add(Pair(category, rememberedAmount.value))
        rememberedAmount.easyEmit(BigDecimal("0"))
    }

    fun submitCategorization() {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend2.value!!
                .copy(categoryAmounts = categoryAmountsRedefined.value)
        ).subscribe()
    }

    fun fill(transaction: Transaction) {
        rememberedAmount.easyEmit(categoryAmountsRedefined.value.defaultAmount(transaction.amount))
    }

    val categoryAmounts = SourceList<Pair<Category, BigDecimal>>()
    val categoryAmountsRedefined = categoryAmounts.flow.map { CategoryAmounts(it.fold(mutableMapOf()) { acc, v -> acc[v.first] = (acc[v.first] ?: BigDecimal("0")) + v.second; acc }) }.stateIn(GlobalScope, SharingStarted.Eagerly, CategoryAmounts())

    // # Model State
    val rememberedAmount = MutableStateFlow(BigDecimal("0"))
    val amountLeftToCategorize =
        combine(
            transactionsInteractor.mostRecentUncategorizedSpend2,
            categoryAmountsRedefined
        ) { transaction, categoryAmounts ->
            categoryAmounts.defaultAmount(transaction?.amount ?: BigDecimal("0"))
        }
}