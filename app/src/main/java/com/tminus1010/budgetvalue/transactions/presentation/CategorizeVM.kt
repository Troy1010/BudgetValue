package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.unbox
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor,
    replaysRepo: ReplaysRepo,
) : ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend.unbox
                .categorize(category)
        )
            .observe(disposables)
    }

    fun userReplay(replay: IReplay) {
        saveTransactionInteractor.saveTransaction(
            replay.categorize(transactionsInteractor.mostRecentUncategorizedSpend.unbox)
        )
            .observe(disposables)
    }

    fun userUndo() {
        saveTransactionInteractor.undo()
            .observe(disposables)
    }

    fun userRedo() {
        saveTransactionInteractor.redo()
            .observe(disposables)
    }

    private lateinit var _categorySelectionVM: CategorySelectionVM
    fun setup(categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
    }

    // # Output
    val matchingReplays =
        Observable.combineLatest(replaysRepo.fetchReplays(), transactionsInteractor.mostRecentUncategorizedSpend)
        { replays, (transaction) ->
            if (transaction == null) emptyList() else
                replays.filter { it.predicate(transaction) }
        }!!
    val isUndoAvailable = saveTransactionInteractor.isUndoAvailable
    val isRedoAvailable = saveTransactionInteractor.isRedoAvailable
    val isTransactionAvailable: Observable<Boolean> =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it.first != null }
    val date: Observable<String> =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
    val latestUncategorizedTransactionAmount: Observable<String> =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it.first?.defaultAmount?.toString() ?: "" }
    val latestUncategorizedTransactionDescription: Observable<String> =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it.first?.description ?: "" }
    val uncategorizedSpendsSize =
        transactionsInteractor.uncategorizedSpends
            .map { it.size.toString() }
}