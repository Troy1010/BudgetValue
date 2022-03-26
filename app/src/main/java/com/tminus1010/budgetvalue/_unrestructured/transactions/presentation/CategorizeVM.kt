package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue._unrestructured.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.app.FuturesInteractor
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.framework.view.SpinnerService
import com.tminus1010.budgetvalue.framework.view.Toaster
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem2
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
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
    categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val toaster: Toaster,
    private val categoriesInteractor: CategoriesInteractor,
    private val spinnerService: SpinnerService,
    selectCategoriesModel: SelectCategoriesModel,
    errors: Errors,
    futuresRepo: FuturesRepo,
    private val futuresInteractor: FuturesInteractor,
) : ViewModel() {
    // # User Intents
    fun userSimpleCategorize(category: Category) {
        saveTransactionInteractor.saveTransaction(
            transactionsInteractor.mostRecentUncategorizedSpend2.value!!.categorize(category)
        )
            .let(spinnerService::decorate)
            .subscribe()
    }

    fun userReplay(future: Future) {
        saveTransactionInteractor.saveTransaction(
            future.categorize(transactionsInteractor.mostRecentUncategorizedSpend2.value!!)
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
            val categoryUnknown = categoriesInteractor.userCategories.take(1).first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            saveTransactionInteractor.saveTransactions(
                transactionsInteractor.uncategorizedSpends2.first().map { it.categorize(categoryUnknown) }
            )
        })
    }

    fun userTryNavToCreateFuture2() {
        navToCreateFuture2.onNext()
    }

    fun userAddTransactionToFuture(future: Future) {
        GlobalScope.launch(block = spinnerService.decorate {
            futuresInteractor.addTransactionDescriptionToFuture(
                description = transactionsInteractor.mostRecentUncategorizedSpend2.value!!.description,
                future = future
            )
                .also { toaster.toast("$it transactions categorized") }
        })
    }

    // # Events
    val navToCreateFuture2 = MutableSharedFlow<Unit>()
    val navToSplit = MutableSharedFlow<Transaction>()
    val navToCategorySettings = MutableSharedFlow<Category>()
    val navToNewCategory = MutableSharedFlow<Unit>()
    val navToReplayOrFutureDetails = MutableSharedFlow<Future>()
    val navToReceiptCategorization = MutableSharedFlow<Transaction>()

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
                                title = "Add Current Transaction Name",
                                onClick = { userAddTransactionToFuture(it) }
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
                            onClick = { navToSplit.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend2.value!!) },
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
                    if (inSelectionMode)
                        ButtonVMItem(
                            title = "Categorize All Matching Descriptions As This Category",
                            isEnabled2 = combine(selectCategoriesModel.selectedCategories.map { it.size == 1 }, isTransactionAvailable) { a, b -> a && b },
                            onClick = {
                                categorizeAllMatchingUncategorizedTransactions(
                                    predicate = { latestUncategorizedTransactionDescription.value!!.uppercase() in it.description.uppercase() },
                                    categorization = { it.categorize(selectCategoriesModel.selectedCategories.value.first()) }
                                ).subscribeBy { toaster.toast("$it transactions categorized") }
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
                            onClick = { navToReceiptCategorization.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend2.value!!) },
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
                    ButtonVMItem(
                        title = "Create Future",
                        isEnabled2 = isTransactionAvailable,
                        onClick = { userTryNavToCreateFuture2() },
                    ),
                )
            }
}