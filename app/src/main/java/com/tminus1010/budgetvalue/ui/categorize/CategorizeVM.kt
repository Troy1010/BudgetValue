package com.tminus1010.budgetvalue.ui.categorize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.takeUntilSignal
import com.tminus1010.budgetvalue.app.*
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.framework.view.SpinnerService
import com.tminus1010.budgetvalue.framework.view.Toaster
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem2
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.budgetvalue.ui.edit_string.EditStringSharedVM
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.select_categories.SelectCategoriesModel
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val transactionsInteractor: TransactionsInteractor,
    private val toaster: Toaster,
    private val categoriesInteractor: CategoriesInteractor,
    private val spinnerService: SpinnerService,
    selectCategoriesModel: SelectCategoriesModel,
    errors: Errors,
    futuresRepo: FuturesRepo,
    private val futuresInteractor: FuturesInteractor,
    private val redoUndoInteractor: RedoUndoInteractor,
    private val editStringSharedVM: EditStringSharedVM,
    private val categorizeAllMatchingUncategorizedTransactionsInteractor: CategorizeAllMatchingUncategorizedTransactionsInteractor,
) : ViewModel() {
    // # User Intents
    fun userSimpleCategorize(category: Category) {
        GlobalScope.launch(block = spinnerService.decorate {
            saveTransactionInteractor.saveTransaction(
                transactionsInteractor.mostRecentUncategorizedSpend.value!!.categorize(category)
            )
        })
    }

    fun userReplay(future: Future) {
        GlobalScope.launch(block = spinnerService.decorate {
            saveTransactionInteractor.saveTransaction(
                future.categorize(transactionsInteractor.mostRecentUncategorizedSpend.value!!)
            )
        })
    }

    fun userUndo() {
        GlobalScope.launch(block = spinnerService.decorate {
            redoUndoInteractor.undo()
        })
    }

    fun userRedo() {
        GlobalScope.launch(block = spinnerService.decorate {
            redoUndoInteractor.redo()
        })
    }

    fun userCategorizeAllAsUnknown() {
        GlobalScope.launch(block = spinnerService.decorate {
            val categoryUnknown = categoriesInteractor.userCategories.take(1).first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            saveTransactionInteractor.saveTransactions(
                transactionsInteractor.uncategorizedSpends.first().map { it.categorize(categoryUnknown) }
            )
        })
    }

    fun userTryNavToCreateFuture2() {
        navToCreateFuture.onNext()
    }

    fun userAddTransactionToFuture(future: Future) {
        GlobalScope.launch(block = spinnerService.decorate {
            futuresInteractor.addTransactionDescriptionToFuture(
                description = transactionsInteractor.mostRecentUncategorizedSpend.value!!.description,
                future = future,
            )
                .also { toaster.toast("$it transactions categorized") }
        })
    }

    fun userAddTransactionToFutureWithEdit(future: Future) {
        editStringSharedVM.userSubmitString.take(1).takeUntilSignal(editStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = spinnerService.decorate { // TODO: There should be a better way than launching within a launch, right?
                futuresInteractor.addTransactionDescriptionToFuture(
                    description = s,
                    future = future,
                )
                    .also { toaster.toast("$it transactions categorized") }
            })
        }
        navToEditStringForAddTransactionToFutureWithEdit.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseDescription(future: Future) {
        GlobalScope.launch(block = spinnerService.decorate {
            categorizeAllMatchingUncategorizedTransactionsInteractor(TransactionMatcher.SearchText(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)::isMatch, future::categorize)
                .also { toaster.toast("$it transactions categorized") }
        })
    }

    fun userUseDescriptionWithEdit(future: Future) {
        editStringSharedVM.userSubmitString.take(1).takeUntilSignal(editStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = spinnerService.decorate { // TODO: There should be a better way than launching within a launch, right?
                categorizeAllMatchingUncategorizedTransactionsInteractor(TransactionMatcher.SearchText(s)::isMatch, future::categorize)
                    .also { toaster.toast("$it transactions categorized") }
            })
        }
        navToEditStringForAddTransactionToFutureWithEdit.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    // # Events
    val navToCreateFuture = MutableSharedFlow<Unit>()
    val navToSplit = MutableSharedFlow<Transaction>()
    val navToCategorySettings = MutableSharedFlow<Category>()
    val navToNewCategory = MutableSharedFlow<Unit>()
    val navToReplayOrFutureDetails = MutableSharedFlow<Future>()
    val navToReceiptCategorization = MutableSharedFlow<Transaction>()
    val navToEditStringForAddTransactionToFutureWithEdit = MutableSharedFlow<String>()

    // # State
    val isUndoAvailable = redoUndoInteractor.isUndoAvailable
    val isRedoAvailable = redoUndoInteractor.isRedoAvailable
    val isTransactionAvailable =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val date =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
    val latestUncategorizedTransactionAmount =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it?.defaultAmount?.toString() }
    val latestUncategorizedTransactionDescription =
        transactionsInteractor.mostRecentUncategorizedSpend
            .map { it?.description }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val uncategorizedSpendsSize =
        transactionsInteractor.uncategorizedSpends
            .map { it.size.toString() }
    val recipeGrid =
        combine(futuresRepo.futures.map { it.filter { it.isAvailableForManual } }, categoriesInteractor.userCategories)
        { nonAutomaticFutures, categories ->
            listOf(
                *categories.map { category ->
                    ButtonVMItem2(
                        title = category.name,
                        alpha = selectCategoriesModel.selectedCategories.map {
                            if (selectCategoriesModel.selectedCategories.value.isEmpty() || category in selectCategoriesModel.selectedCategories.value)
                                1F
                            else
                                0.5F
                        },
                        onClick = {
                            if (selectCategoriesModel.selectedCategories.value.isNotEmpty())
                                if (category in selectCategoriesModel.selectedCategories.value)
                                    selectCategoriesModel.unselectCategories(category)
                                else
                                    selectCategoriesModel.selectCategories(category)
                            else
                                userSimpleCategorize(category)
                        },
                        onLongClick = {
                            if (category in selectCategoriesModel.selectedCategories.value)
                                selectCategoriesModel.unselectCategories(category)
                            else
                                selectCategoriesModel.selectCategories(category)
                        },
                    )
                }.toTypedArray(),
                *nonAutomaticFutures.map {
                    ButtonVMItem2(
                        title = it.name,
                        backgroundColor = R.attr.colorSecondary,
                        onClick = { userReplay(it) },
                        menuVMItems = MenuVMItems(
                            MenuVMItem(
                                title = "Add Description",
                                onClick = { userAddTransactionToFuture(it) }
                            ),
                            MenuVMItem(
                                title = "Add Description With Edit",
                                onClick = { userAddTransactionToFutureWithEdit(it) }
                            ),
                            MenuVMItem(
                                title = "Use Description",
                                onClick = { userUseDescription(it) }
                            ),
                            MenuVMItem(
                                title = "Use Description With Edit",
                                onClick = { userUseDescriptionWithEdit(it) }
                            ),
                            MenuVMItem(
                                title = "Edit",
                                onClick = { navToReplayOrFutureDetails.onNext(it) }
                            ),
                        ),
                    )
                }.toTypedArray(),
            )
        }
            .divertErrors(errors)
    val buttons =
        selectCategoriesModel.selectedCategories.map { it.isNotEmpty() }.asObservable2()
            .map { inSelectionMode ->
                listOfNotNull(
                    if (inSelectionMode)
                        ButtonVMItem(
                            title = "Split",
                            isEnabled2 = isTransactionAvailable,
                            onClick = { navToSplit.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend.value!!) },
                        )
                    else null,
                    if (inSelectionMode)
                        ButtonVMItem(
                            title = "Category Settings",
                            isEnabled = selectCategoriesModel.selectedCategories.asObservable2().map { it.size == 1 },
                            onClick = {
                                navToCategorySettings.easyEmit(selectCategoriesModel.selectedCategories.value.first())
                                runBlocking { selectCategoriesModel.clearSelection() }
                            }
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
                            onClick = { navToReceiptCategorization.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend.value!!) },
                        )
                    else null,
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Redo",
                            isEnabled2 = isRedoAvailable,
                            onClick = { userRedo() },
                        )
                    else null,
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Undo",
                            isEnabled2 = isUndoAvailable,
                            onClick = { userUndo() },
                        )
                    else null,
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Create Category",
                            onClick = { navToNewCategory.easyEmit(Unit) }
                        )
                    else null,
                    ButtonVMItem(
                        title = "Create Future",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { userTryNavToCreateFuture2() },
                    ),
                )
            }
}
