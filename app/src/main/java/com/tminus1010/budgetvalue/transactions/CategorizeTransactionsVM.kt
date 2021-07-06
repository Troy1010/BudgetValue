package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
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
    private val saveTransactionDomain: SaveTransactionDomain,
    private val transactionsRepo: ITransactionsRepo,
    transactionsDomain: TransactionsDomain
) : ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        saveTransactionDomain.saveTransaction(
            firstTransactionBox.value!!.first!!
                .categorize(category)
        )
            .observe(disposables)
    }

    fun userReplay() {
        saveTransactionDomain.saveTransaction(
            firstTransactionBox.value!!.first!!
                .categorize(
                    categoryAmounts = replayTransactionBox.value!!.first!!
                        .calcCAsAdjustedForNewTotal(firstTransactionBox.value!!.first!!.amount)
                )
        )
            .observe(disposables)
    }

    fun userUndo() {
        saveTransactionDomain.undo()
            .observe(disposables)
    }

    fun userRedo() {
        saveTransactionDomain.redo()
            .observe(disposables)
    }

    fun userNavToSplitWithReplayValues() {
        _categorySelectionVM.clearSelection()
        _categorySelectionVM.selectCategories(*replayTransactionBox.value!!.first!!.categoryAmounts.map { it.key }.toTypedArray())
        navToSplit.onNext(replayTransactionBox.value!!.first!!.calcCAsAdjustedForNewTotal(firstTransactionBox.value!!.first!!.amount))
    }

    fun setup(categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
    }

    // # Internal
    private lateinit var _categorySelectionVM: CategorySelectionVM
    private val firstTransactionBox =
        transactionsDomain.uncategorizedSpends
            .map { Box(it.getOrNull(0)) }
            .nonLazyCache(disposables)
    private val replayTransactionBox =
        firstTransactionBox
            .unbox()
            .flatMapSingle { transaction ->
                transactionsRepo.findTransactionsWithDescription(transaction.description)
                    .map { transactionsWithMatchingDescription ->
                        transactionsWithMatchingDescription
                            .filter { transaction.id != it.id && it.categorizationDate != null }
                            .let { Box(it.maxByOrNull { it.categorizationDate!! }) }
                    }
            }
            .nonLazyCache(disposables)

    // # Output
    val isReplayAvailable: Observable<Boolean> = replayTransactionBox
        .map { it.first != null }
        .startWithItem(false)
        .nonLazyCache(disposables)
    val isUndoAvailable = saveTransactionDomain.isUndoAvailable
    val isRedoAvailable = saveTransactionDomain.isRedoAvailable
    val amountToCategorize = firstTransactionBox.unbox()
        .map { "Amount to categorize: $${it.amount}" }
        .nonLazyCache(disposables)
        .divertErrors(errorSubject)
    val isTransactionAvailable = firstTransactionBox
        .map { it.first != null }
        .divertErrors(errorSubject)
    val date = firstTransactionBox
        .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        .divertErrors(errorSubject)
    val latestUncategorizedTransactionAmount = firstTransactionBox
        .map { it.first?.defaultAmount?.toString() ?: "" }
        .divertErrors(errorSubject)
    val latestUncategorizedTransactionDescription = firstTransactionBox
        .map { it.first?.description ?: "" }
        .divertErrors(errorSubject)
    val navToSplit = PublishSubject.create<Map<Category, BigDecimal>>()
    val transactionBox = firstTransactionBox
        .nonLazyCache(disposables)
        .divertErrors(errorSubject)
}
