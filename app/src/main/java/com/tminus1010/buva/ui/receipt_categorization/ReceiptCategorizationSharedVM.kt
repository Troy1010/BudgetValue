package com.tminus1010.buva.ui.receipt_categorization

import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.all_layers.source_objects.SourceList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptCategorizationSharedVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
) {
    val total = MutableSharedFlow<BigDecimal>(1)
    fun submitPartialCategorization(category: Category) {
        categoryAmounts.add(Pair(category, rememberedAmount.value))
        rememberedAmount.easyEmit(BigDecimal("0"))
    }

    fun fill() {
        rememberedAmount.easyEmit(amountLeftToCategorize.value)
    }

    fun submitCategorization(transaction: Transaction) = runBlocking {
        transactionsInteractor.push(transaction.copy(categoryAmounts = categoryAmountsRedefined.value))
    }

    val userSubmitCategorization = MutableSharedFlow<CategoryAmounts>()
    fun userSubmitCategorization() {
        userSubmitCategorization.onNext(categoryAmountsRedefined.value)
    }

    val categoryAmounts = SourceList<Pair<Category, BigDecimal>>()
    val categoryAmountsRedefined =
        categoryAmounts.flow
            .map { CategoryAmounts(it.fold(mutableMapOf()) { acc, v -> acc[v.first] = (acc[v.first] ?: BigDecimal("0")) + v.second; acc }) }
            .stateIn(GlobalScope, SharingStarted.Eagerly, CategoryAmounts())
    val rememberedAmount = MutableStateFlow(BigDecimal("0"))
    val amountLeftToCategorize =
        combine(total, categoryAmountsRedefined)
        { total, categoryAmounts ->
            categoryAmounts.defaultAmount(total)
        }
            .stateIn(GlobalScope, SharingStarted.Eagerly, BigDecimal("0"))
}