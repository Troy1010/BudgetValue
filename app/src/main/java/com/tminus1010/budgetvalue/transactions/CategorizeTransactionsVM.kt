package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val categorizeTransactionsDomain: CategorizeTransactionsDomain,
    private val transactionsRepo: ITransactionsRepo,
    transactionsDomain: TransactionsDomain
): ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        categorizeTransactionsDomain.submitCategorization(
            id = firstTransactionBox.value!!.first!!.id,
            category = category
        )
            .observe(disposables)
    }
    fun userReplay() {
        categorizeTransactionsDomain.submitCategorization(
            id = firstTransactionBox.value!!.first!!.id,
            categoryAmounts = replayTransactionBox.value!!.first!!.categoryAmounts,
        )
            .observe(disposables)
    }
    fun userUndo() {
        categorizeTransactionsDomain.undo()
            .observe(disposables)
    }
    fun userRedo() {
        categorizeTransactionsDomain.redo()
            .observe(disposables)
    }
    fun userNavToSplitWithReplayValues() {
        navToSplit.onNext(replayTransactionBox.value!!.first!!.categoryAmounts)
    }
    // # Internal
    private val firstTransactionBox =
        transactionsDomain.uncategorizedSpends
            .map { Box(it.getOrNull(0)) }
            .nonLazyCache(disposables)
    private val replayTransactionBox =
        firstTransactionBox
            .unbox()
            .flatMapSingle { transaction ->
                transactionsRepo.findTransactionsWithDescription(transaction.description)
                    .map { it.filter { transaction.id != it.id && !it.isUncategorized } }
            }
            .map { Box(it.maxByOrNull { it.date }) } // This will redo the transaction that happened most recent. But perhaps I should remember when the categorization took place, and redo the most recent.
            .nonLazyCache(disposables)
    // # Output
    val isReplayAvailable: Observable<Boolean> = replayTransactionBox
        .map { it.first != null }
        .startWithItem(false)
        .nonLazyCache(disposables)
    val isUndoAvailable = categorizeTransactionsDomain.isUndoAvailable
    val isRedoAvailable = categorizeTransactionsDomain.isRedoAvailable
    val amountToCategorize = firstTransactionBox.unbox()
        .map { "Amount to categorize: $${it.amount}" }
        .nonLazyCache(disposables)
        .divertErrors(errorSubject)
    val isTransactionAvailable = firstTransactionBox
        .map { it.unbox != null }
        .divertErrors(errorSubject)
    val date = firstTransactionBox
        .map { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        .divertErrors(errorSubject)
    val latestUncategorizedTransactionAmount = firstTransactionBox
        .map { it.unbox?.defaultAmount?.toString() ?: "" }
        .divertErrors(errorSubject)
    val latestUncategorizedTransactionDescription = firstTransactionBox
        .map { it.unbox?.description ?: "" }
        .divertErrors(errorSubject)
    val navToSplit = PublishSubject.create<Map<Category, BigDecimal>>()
    val transactionBox = firstTransactionBox
        .nonLazyCache(disposables)
        .divertErrors(errorSubject)
}
