package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue._core.data.Repo
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

class TransactionUseCasesImpl @Inject constructor(
    private val repo: Repo,
    private val categoryAmountsConverter: CategoryAmountsConverter,
): TransactionUseCases {
    override val transactions: Observable<List<Transaction>> =
        repo.fetchTransactions()
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()

    override fun tryPush(transaction: Transaction): Completable =
        repo.tryAdd(transaction.toDTO(categoryAmountsConverter))

    override fun tryPush(transactions: List<Transaction>): Completable =
        repo.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) })

    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable =
        transaction.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }) }

    override fun pushTransactionCAs(transaction: Transaction, categoryAmounts: Map<Category, BigDecimal>) =
        repo.updateTransactionCategoryAmounts(transaction.id, categoryAmounts.mapKeys { it.key.name })
}