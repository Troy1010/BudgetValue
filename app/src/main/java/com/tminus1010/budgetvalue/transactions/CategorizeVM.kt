package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplayRepo
import com.tminus1010.budgetvalue.replay_or_future.models.IReplay
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val transactionsDomain: TransactionsDomain,
    replayRepo: ReplayRepo,
) : ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        saveTransactionDomain.saveTransaction(
            transactionsDomain.firstUncategorizedSpend.unbox
                .categorize(category)
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

    fun setup(categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
    }

    // # Internal
    private lateinit var _categorySelectionVM: CategorySelectionVM

    // # Output
    val matchingReplays =
        Observables.combineLatest(
            replayRepo.fetchReplays(),
            transactionsDomain.firstUncategorizedSpend,
        )
            .map { (replays, transactionBox) ->
                transactionBox.first
                    ?.let { transaction -> replays.filter { it.predicate(transaction) } }
                    ?: emptyList()
            }!!
    val isUndoAvailable = saveTransactionDomain.isUndoAvailable
    val isRedoAvailable = saveTransactionDomain.isRedoAvailable
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
}
