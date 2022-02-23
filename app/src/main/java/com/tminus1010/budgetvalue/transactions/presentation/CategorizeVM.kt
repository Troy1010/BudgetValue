package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.framework.view.SpinnerService
import com.tminus1010.budgetvalue._core.framework.view.Toaster
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
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
    // # Input
    val inSelectionMode = BehaviorSubject.create<Boolean>()
    val selectedCategories = BehaviorSubject.create<List<Category>>()

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
        GlobalScope.launch {
            spinnerService.asyncTaskStarted()
            val categoryUnknown = categoriesInteractor.userCategories2.take(1).first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            saveTransactionInteractor.saveTransactions(
                transactionsInteractor.uncategorizedSpends2.first().map { it.categorize(categoryUnknown) }
            )
            spinnerService.asyncTaskEnded()
        }
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
    val navToReceiptCategorization = MutableSharedFlow<Transaction>()

    // # Mediation
    val clearSelection = MutableSharedFlow<Unit>()

    // # State
    val matchingReplays =
        Observable.combineLatest(replaysRepo.fetchReplays(), transactionsInteractor.mostRecentUncategorizedSpend)
        { replays, (transaction) ->
            if (transaction == null) emptyList() else
                replays.filter { it.predicate(transaction) }
        }
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
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Categorize All Matching Descriptions As This Category",
                        isEnabled = selectedCategories.map { it.size == 1 },
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
                        title = "Categorize all as Unknown",
                        isEnabled = isTransactionAvailable,
                        onClick = { userCategorizeAllAsUnknown() },
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Do Receipt Categorization",
                        isEnabled = isTransactionAvailable,
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
                        title = "Make New Category",
                        onClick = { navToNewCategory.easyEmit(Unit) }
                    )
                else null,
            )
        }
}
