package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.onNext
import com.tminus1010.budgetvalue._core.framework.view.SpinnerService
import com.tminus1010.budgetvalue._core.framework.view.Toaster
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor,
    replaysRepo: ReplaysRepo,
    categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val toaster: Toaster,
    private val categoriesInteractor: CategoriesInteractor,
    private val spinnerService: SpinnerService,
) : ViewModel() {
    // # Setup
    val selectedCategories = BehaviorSubject.create<List<Category>>()

    // # User Intents
    fun userSimpleCategorize(category: Category) {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend2.value!!.categorize(category)
        )
            .let(spinnerService::decorate)
            .subscribe()
    }

    fun userReplay(replay: IReplay) {
        saveTransactionInteractor.saveTransaction(
            replay.categorize(transactionsInteractor.mostRecentUncategorizedSpend2.value!!)
        )
            .let(spinnerService::decorate)
            .subscribe()
    }

    fun userUndo() {
        saveTransactionInteractor.undo()
            .let(spinnerService::decorate)
            .subscribe()
    }

    fun userRedo() {
        saveTransactionInteractor.redo()
            .let(spinnerService::decorate)
            .subscribe()
    }

    fun userCategorizeAllAsUnknown() {
        GlobalScope.launch(block = spinnerService.decorate {
            val categoryUnknown = categoriesInteractor.userCategories2.take(1).first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            saveTransactionInteractor.saveTransactions(
                transactionsInteractor.uncategorizedSpends2.first().map { it.categorize(categoryUnknown) }
            )
        })
    }

    fun userTryNavToCreateFuture2() {
        navToCreateFuture2.onNext()
    }

    // # Events
    val navToCreateFuture2 = MutableSharedFlow<Unit>()
    val navToSplit = MutableSharedFlow<Transaction>()
    val navToCategorySettings = MutableSharedFlow<Category>()
    val navToNewCategory = MutableSharedFlow<Unit>()
    val navToReplay = MutableSharedFlow<IReplay>()
    val navToSelectReplay = MutableSharedFlow<Unit>()
    val navToReceiptCategorization = MutableSharedFlow<Transaction>()

    // # Mediation
    val clearSelection = MutableSharedFlow<Unit>()

    // # Internal
    private val matchingReplays =
        combine(replaysRepo.fetchReplays().asFlow(), transactionsInteractor.mostRecentUncategorizedSpend2)
        { replays, transaction ->
            if (transaction == null) emptyList() else
                replays.filter { it.shouldCategorizeOnImport(transaction) }
        }

    // # State
    val isUndoAvailable = saveTransactionInteractor.isUndoAvailable
    val isRedoAvailable = saveTransactionInteractor.isRedoAvailable
    val isTransactionAvailable =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val date =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .map { it?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
    val latestUncategorizedTransactionAmount =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .map { it?.defaultAmount?.toString() }
    val latestUncategorizedTransactionDescription =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .map { it?.description }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val uncategorizedSpendsSize =
        transactionsInteractor.uncategorizedSpends2
            .map { it.size.toString() }
    val buttons =
        Observable.combineLatest(selectedCategories.map { it.isNotEmpty() }, matchingReplays.asObservable())
        { inSelectionMode, matchingReplays ->
            listOfNotNull(
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Split",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { navToSplit.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend2.value!!) },
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
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Categorize All Matching Descriptions As This Category",
                        isEnabled2 = combine(selectedCategories.map { it.size == 1 }.asFlow(), isTransactionAvailable) { a, b -> a && b },
                        onClick = {
                            categorizeAllMatchingUncategorizedTransactions(
                                predicate = { latestUncategorizedTransactionDescription.value!!.uppercase() in it.description.uppercase() },
                                categorization = { it.categorize(selectedCategories.value!!.first()) }
                            ).subscribeBy { toaster.toast("$it transactions categorized") }
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
                        title = "Create Future",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { userTryNavToCreateFuture2() },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Categorize all as Unknown",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { userCategorizeAllAsUnknown() },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Do Receipt Categorization",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { navToReceiptCategorization.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend2.value!!) },
                    )
                else null,
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
                        title = "Create New Category",
                        onClick = { navToNewCategory.easyEmit(Unit) }
                    )
                else null,
            )
        }
}
