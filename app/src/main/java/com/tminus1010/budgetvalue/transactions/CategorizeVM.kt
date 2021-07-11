package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay.models.IReplay
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// TODO: Some of these input/output are no longer used, can probably be deleted
@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val transactionsRepo: TransactionsRepo,
    private val transactionsDomain: TransactionsDomain
) : ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        saveTransactionDomain.saveTransaction(
            transactionsDomain.firstUncategorizedSpend.unbox
                .categorize(category)
        )
            .observe(disposables)
    }

    fun userReplay() {
        saveTransactionDomain.saveTransaction(
            transactionsDomain.firstUncategorizedSpend.unbox
                .categorize(
                    categoryAmounts = replayTransactionBox.unbox
                        .calcCAsAdjustedForNewTotal(transactionsDomain.firstUncategorizedSpend.unbox.amount)
                )
        )
            .observe(disposables)
    }

    fun userReplay(replay: IReplay) {
        saveTransactionDomain.saveTransaction(
            replay.categorize(transactionsDomain.firstUncategorizedSpend.unbox)
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

    fun userNavToSplitWithReplayByPercentage() {
        _categorySelectionVM.clearSelection()
        _categorySelectionVM.selectCategories(*replayTransactionBox.value!!.first!!.categoryAmounts.map { it.key }.toTypedArray())
        navToSplit.onNext(replayTransactionBox.value!!.first!!.calcCAsAdjustedForNewTotal(transactionsDomain.firstUncategorizedSpend.unbox.amount))
    }

    fun setup(categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
    }

    // # Internal
    private lateinit var _categorySelectionVM: CategorySelectionVM
    private val replayTransactionBox =
        transactionsDomain.firstUncategorizedSpend
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
    val amountToCategorize =
        transactionsDomain.firstUncategorizedSpend
            .unbox()
            .map { "Amount to split: $${it.amount}" }
            .nonLazyCache(disposables)
    val isTransactionAvailable: Observable<Boolean> =
        transactionsDomain.firstUncategorizedSpend
            .map { it.first != null }
    val date: Observable<String> =
        transactionsDomain.firstUncategorizedSpend
            .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
    val latestUncategorizedTransactionAmount: Observable<String> =
        transactionsDomain.firstUncategorizedSpend
            .map { it.first?.defaultAmount?.toString() ?: "" }
    val latestUncategorizedTransactionDescription: Observable<String> =
        transactionsDomain.firstUncategorizedSpend
            .map { it.first?.description ?: "" }
    val navToSplit = PublishSubject.create<Map<Category, BigDecimal>>()!!
}