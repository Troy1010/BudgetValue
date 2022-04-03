package com.tminus1010.budgetvalue.ui.receipt_categorization

import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Can I make this more similar to the other SharedVMs?
@Singleton
class ReceiptCategorizationSharedVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
) {
    // # Model Action
    fun submitPartialCategorization(category: Category) {
        categoryAmounts.add(Pair(category, rememberedAmount.value))
        rememberedAmount.easyEmit(BigDecimal("0"))
    }

    fun submitCategorization() {
        GlobalScope.launch {
            transactionsInteractor.push(
                transactionsInteractor.mostRecentUncategorizedSpend.value!!
                    .copy(categoryAmounts = categoryAmountsRedefined.value)
            )
        }
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
            transactionsInteractor.mostRecentUncategorizedSpend,
            categoryAmountsRedefined
        ) { transaction, categoryAmounts ->
            categoryAmounts.defaultAmount(transaction?.amount ?: BigDecimal("0"))
        }
}