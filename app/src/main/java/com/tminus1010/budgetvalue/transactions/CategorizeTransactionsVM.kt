package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.extensions.toSingle
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.toState
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val categorizeTransactionsDomain: CategorizeTransactionsDomain,
    private val transactionsRepo: ITransactionsRepo
): ViewModel() {
    // # State
    val amountToCategorize = categorizeTransactionsDomain.transactionBox.unbox()
        .map { "Amount to categorize: $${it.amount}" }
        .divertErrors(errorSubject).nonLazyCache(disposables)
    val isTransactionAvailable = categorizeTransactionsDomain.transactionBox
        .map { it.unbox != null }
        .toLiveData(errorSubject)
    val date = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        .toLiveData(errorSubject)
    val latestUncategorizedTransactionAmount = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.defaultAmount?.toString() ?: "" }
        .toLiveData(errorSubject)
    val latestUncategorizedTransactionDescription = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.description ?: "" }
        .toLiveData(errorSubject)
    val matchingDescriptions = categorizeTransactionsDomain.transactionBox.unbox()
        .flatMapSingle { transaction ->
            transactionsRepo.findTransactionsWithDescription(transaction.description)
                .map { it.filter { transaction.id != it.id && !it.isUncategorized } }
        }
    val redoTransaction = matchingDescriptions
        .map { Box(it.maxByOrNull { it.date }) } // This will redo the transaction that happened most recent. But perhaps I should remember when the categorization took place, and redo the most recent.
    val isRedoAvailable = redoTransaction
        .map { it.first != null }
        .nonLazyCache(disposables)
    val navToSplit = PublishSubject.create<Map<Category, BigDecimal>>()
    val transactionBox = categorizeTransactionsDomain.transactionBox
        .toState(disposables, errorSubject)
    // # Intents
    fun finishTransactionWithCategory(category: Category) {
        categorizeTransactionsDomain.finishTransactionWithCategory(category)
    }
    fun redo() {
        Singles.zip(
            categorizeTransactionsDomain.transactionBox.unbox().toSingle(),
            redoTransaction.toSingle()
        ).subscribeOn(Schedulers.io())
            .flatMapCompletable { (transaction, redoTransaction) ->
                transactionsRepo.pushTransactionCAs(
                    transaction,
                    redoTransaction.unbox!!.categoryAmounts
                )
            }
            .subscribe()
    }
    fun tryNavToSplitWithRedoValues() {
        redoTransaction.toSingle()
            .observe(disposables, onSuccess = { navToSplit.onNext(it.first!!.categoryAmounts) })
    }
}
