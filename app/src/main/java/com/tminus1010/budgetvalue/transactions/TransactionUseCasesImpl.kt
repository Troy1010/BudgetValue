package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue._core.data.RepoFacade
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

//class TransactionUseCasesImpl @Inject constructor(
//    private val repoFacade: RepoFacade,
//    private val categoryAmountsConverter: CategoryAmountsConverter,
//): TransactionUseCases {
//    override val transactions: Observable<List<Transaction>> =
//        repoFacade.fetchTransactions()
//            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
//            .replay(1).refCount()
//
//    override fun tryPush(transaction: Transaction): Completable =
//        repoFacade.tryAdd(transaction.toDTO(categoryAmountsConverter))
//
//    override fun tryPush(transactions: List<Transaction>): Completable =
//        repoFacade.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) })
//
//    override fun pushTransactionCA(transaction: Transaction, category: Category, amount: BigDecimal?): Completable =
//        transaction.categoryAmounts
//            .toMutableMap()
//            .apply { if (amount==null) remove(category) else put(category, amount) }
//            .let { repoFacade.updateTransactionCategoryAmounts(transaction.id, it.mapKeys { it.key.name }) }
//
//    override fun pushTransactionCAs(transaction: Transaction, categoryAmounts: Map<Category, BigDecimal>) =
//        repoFacade.updateTransactionCategoryAmounts(transaction.id, categoryAmounts.mapKeys { it.key.name })
//}