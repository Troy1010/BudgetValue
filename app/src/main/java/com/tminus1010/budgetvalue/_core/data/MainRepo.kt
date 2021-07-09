package com.tminus1010.budgetvalue._core.data

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper,
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) : IMainRepo {
    override fun fetchAppInitBool(): Boolean =
        sharedPrefWrapper.fetchAppInitBool()

    override fun pushAppInitBool(appInitBool: Boolean): Completable =
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())

    override val anchorDateOffset: Observable<Long> =
        sharedPrefWrapper.anchorDateOffset
            .subscribeOn(Schedulers.io())

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        sharedPrefWrapper.pushAnchorDateOffset(anchorDateOffset)
            .subscribeOn(Schedulers.io())

    override val blockSize: Observable<Long> =
        sharedPrefWrapper.blockSize
            .subscribeOn(Schedulers.io())

    override fun pushBlockSize(blockSize: Long?): Completable =
        sharedPrefWrapper.pushBlockSize(blockSize)
            .subscribeOn(Schedulers.io())

    override val transactions: Observable<List<Transaction>> =
        miscDAO.fetchTransactions()
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()
            .subscribeOn(Schedulers.io())

    override fun tryPush(transaction: Transaction): Completable =
        miscDAO.tryAdd(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun push(transaction: Transaction): Completable =
        miscDAO.add(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun delete(transaction: Transaction): Completable =
        miscDAO.delete(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun update(transaction: Transaction): Completable =
        miscDAO.update(transaction.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun tryPush(transactions: List<Transaction>): Completable =
        miscDAO.tryAdd(transactions.map { it.toDTO(categoryAmountsConverter) })
            .subscribeOn(Schedulers.io())

    override fun findTransactionsWithDescription(description: String): Single<List<Transaction>> =
        miscDAO.fetchTransactions(description)
            .map { it.map { Transaction.fromDTO(it, categoryAmountsConverter) } }
            .subscribeOn(Schedulers.io())

    override fun getTransaction(id: String): Single<Transaction> =
        miscDAO.getTransaction(id)
            .map { Transaction.fromDTO(it, categoryAmountsConverter) }
            .subscribeOn(Schedulers.io())
}