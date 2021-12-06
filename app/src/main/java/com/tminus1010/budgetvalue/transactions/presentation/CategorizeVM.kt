package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.unbox
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor,
    replaysRepo: ReplaysRepo,
) : ViewModel() {
    // # Input
    val inSelectionMode = BehaviorSubject.create<Boolean>()
    val selectedCategories = BehaviorSubject.create<List<Category>>()

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

    // # Presentation Events
    val navToCreateFuture = MutableSharedFlow<Unit>()
    val navToSplit = MutableSharedFlow<Transaction>()
    val navToCategorySettings = MutableSharedFlow<Category>()
    val navToNewCategory = MutableSharedFlow<Unit>()
    val navToReplay = MutableSharedFlow<IReplay>()
    val navToSelectReplay = MutableSharedFlow<Unit>()

    // # Mediation
    val clearSelection = MutableSharedFlow<Unit>()

    // # Presentation State
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
    val buttons =
        Observable.combineLatest(inSelectionMode, matchingReplays)
        { inSelectionMode, matchingReplays ->
            listOfNotNull(
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Create Future",
                        onClick = { navToCreateFuture.easyEmit(Unit) },
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Split",
                        isEnabled = isTransactionAvailable,
                        onClick = { navToSplit.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend.value!!.first!!) },
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Category Settings",
                        isEnabled = selectedCategories.map { it.size == 1 },
                        onClick = {
                            navToCategorySettings.easyEmit(selectedCategories.value!!.first())
                            clearSelection.easyEmit(Unit)
                        }
                    )
                else null,
                *(if (inSelectionMode)
                    emptyList()
                else
                    matchingReplays
                        .map { replay ->
                            ButtonVMItem(
                                title = "Replay (${replay.name})",
                                onClick = { userReplay(replay) },
                                onLongClick = { navToReplay.easyEmit(replay) },
                            )
                        })
                    .toTypedArray(),
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Use Replay",
                        onClick = { navToSelectReplay.easyEmit(Unit) },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Redo",
                        isEnabled = isRedoAvailable,
                        onClick = { userRedo() },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Undo",
                        isEnabled = isUndoAvailable,
                        onClick = { userUndo() },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Make New Category",
                        onClick = { navToNewCategory.easyEmit(Unit) }
                    )
                else null,
            )
        }
}
