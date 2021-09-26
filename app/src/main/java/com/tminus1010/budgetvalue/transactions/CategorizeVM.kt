package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.models.IReplay
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val transactionsAppService: TransactionsAppService,
    replaysRepo: ReplaysRepo,
) : ViewModel() {
    // # Input
    fun userSimpleCategorize(category: Category) {
        saveTransactionDomain.saveTransaction(
            transactionsAppService.mostRecentUncategorizedSpend.unbox
                .categorize(category)
        )
            .observe(disposables)
    }

    fun userReplay(replay: IReplay) {
        saveTransactionDomain.saveTransaction(
            replay.categorize(transactionsAppService.mostRecentUncategorizedSpend.unbox)
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

    private lateinit var _categorySelectionVM: CategorySelectionVM
    fun setup(categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
    }

    // # Output
    val matchingReplays =
        Observable.combineLatest(replaysRepo.fetchReplays(), transactionsAppService.mostRecentUncategorizedSpend)
        { replays, (transaction) ->
            if (transaction == null) emptyList() else
                replays.filter { it.predicate(transaction) }
        }!!
    val isUndoAvailable = saveTransactionDomain.isUndoAvailable
    val isRedoAvailable = saveTransactionDomain.isRedoAvailable
    val isTransactionAvailable: Observable<Boolean> =
        transactionsAppService.mostRecentUncategorizedSpend
            .map { it.first != null }
    val date: Observable<String> =
        transactionsAppService.mostRecentUncategorizedSpend
            .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
    val latestUncategorizedTransactionAmount: Observable<String> =
        transactionsAppService.mostRecentUncategorizedSpend
            .map { it.first?.defaultAmount?.toString() ?: "" }
    val latestUncategorizedTransactionDescription: Observable<String> =
        transactionsAppService.mostRecentUncategorizedSpend
            .map { it.first?.description ?: "" }
}
