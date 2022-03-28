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
import com.tminus1010.budgetvalue.framework.androidx.ShowToast
import com.tminus1010.budgetvalue.ui.all_features.ThrobberSharedVM
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem2
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.set_string.SetStringSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val showToast: ShowToast,
    private val categoriesInteractor: CategoriesInteractor,
    private val throbberSharedVM: ThrobberSharedVM,
    private val chooseCategoriesSharedVM: ChooseCategoriesSharedVM,
    errors: Errors,
    futuresRepo: FuturesRepo,
    private val futuresInteractor: FuturesInteractor,
    private val redoUndoInteractor: RedoUndoInteractor,
    private val setStringSharedVM: SetStringSharedVM,
    private val categorizeMatchingUncategorizedTransactions: CategorizeMatchingUncategorizedTransactions,
) : ViewModel() {
    // # User Intents
    fun userSimpleCategorize(category: Category) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            transactionsInteractor.saveTransactions(
                transactionsInteractor.mostRecentUncategorizedSpend.value!!.categorize(category)
            )
        })
    }

    fun userReplay(future: Future) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            transactionsInteractor.saveTransactions(
                future.categorize(transactionsInteractor.mostRecentUncategorizedSpend.value!!)
            )
        })
    }

    fun userUndo() {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            redoUndoInteractor.undo()
        })
    }

    fun userRedo() {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            redoUndoInteractor.redo()
        })
    }

    fun userCategorizeAllAsUnknown() {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            val categoryUnknown = categoriesInteractor.userCategories.take(1).first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            transactionsInteractor.saveTransactions(
                transactionsInteractor.uncategorizedSpends.first().map { it.categorize(categoryUnknown) }
            )
        })
    }

    fun userTryNavToCreateFuture2() {
        navToCreateFuture.onNext()
    }

    fun userAddTransactionToFuture(future: Future) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            futuresInteractor.addDescriptionToFutureAndCategorize(
                description = transactionsInteractor.mostRecentUncategorizedSpend.value!!.description,
                future = future,
            )
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userAddTransactionToFutureWithEdit(future: Future) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                futuresInteractor.addDescriptionToFutureAndCategorize(
                    description = s,
                    future = future,
                )
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToEditStringForAddTransactionToFutureWithEdit.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseDescription(future: Future) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            categorizeMatchingUncategorizedTransactions(TransactionMatcher.SearchText(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)::isMatch, future::categorize)
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userUseDescriptionWithEdit(future: Future) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                categorizeMatchingUncategorizedTransactions(TransactionMatcher.SearchText(s)::isMatch, future::categorize)
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToEditStringForAddTransactionToFutureWithEdit.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseDescriptionOnCategory(category: Category) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            categorizeMatchingUncategorizedTransactions(TransactionMatcher.SearchText(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)::isMatch, categorize = { it.categorize(category) })
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userUseDescriptionWithEditOnCategory(category: Category) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                categorizeMatchingUncategorizedTransactions(TransactionMatcher.SearchText(s)::isMatch, categorize = { it.categorize(category) })
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToEditStringForAddTransactionToFutureWithEdit.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userTryNavToCategorySettings(category: Category) {
        navToCategorySettings.easyEmit(category)
    }

    // # Events
    val navToCreateFuture = MutableSharedFlow<Unit>()
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
                        alpha = chooseCategoriesSharedVM.selectedCategories.map {
                            if (chooseCategoriesSharedVM.selectedCategories.value.isEmpty() || category in chooseCategoriesSharedVM.selectedCategories.value)
                                1F
                            else
                                0.5F
                        },
                        onClick = {
                            if (chooseCategoriesSharedVM.selectedCategories.value.isNotEmpty())
                                if (category in chooseCategoriesSharedVM.selectedCategories.value)
                                    chooseCategoriesSharedVM.unselectCategories(category)
                                else
                                    chooseCategoriesSharedVM.selectCategories(category)
                            else
                                userUseDescriptionOnCategory(category)
                        },
                        menuVMItemsFlow = chooseCategoriesSharedVM.selectedCategories.map { selectedCategories ->
                            MenuVMItems(
                                if (category in selectedCategories)
                                    MenuVMItem(
                                        title = "Unselect",
                                        onClick = { chooseCategoriesSharedVM.unselectCategories(category) },
                                    )
                                else
                                    MenuVMItem(
                                        title = "Select",
                                        onClick = { chooseCategoriesSharedVM.selectCategories(category) },
                                    ),
                                MenuVMItem(
                                    title = "Edit",
                                    onClick = { userTryNavToCategorySettings(category) }
                                ),
                                MenuVMItem(
                                    title = "Use Only Once",
                                    onClick = { userSimpleCategorize(category) }
                                ),
                                MenuVMItem(
                                    title = "Use With Edit",
                                    onClick = { userUseDescriptionWithEditOnCategory(category) }
                                ),
                            )
                        },
                    )
                }.toTypedArray(),
                *nonAutomaticFutures.map { future ->
                    ButtonVMItem2(
                        title = future.name,
                        backgroundColor = R.attr.colorSecondary,
                        onClick = { userUseDescription(future) },
                        menuVMItems = MenuVMItems(
                            MenuVMItem(
                                title = "Edit",
                                onClick = { navToReplayOrFutureDetails.onNext(future) }
                            ),
                            MenuVMItem(
                                title = "Add",
                                onClick = { userAddTransactionToFuture(future) }
                            ),
                            MenuVMItem(
                                title = "Add With Edit",
                                onClick = { userAddTransactionToFutureWithEdit(future) }
                            ),
                            MenuVMItem(
                                title = "Use Only Once",
                                onClick = { userReplay(future) }
                            ),
                            MenuVMItem(
                                title = "Use With Edit",
                                onClick = { userUseDescriptionWithEdit(future) }
                            ),
                        ),
                    )
                }.toTypedArray(),
            )
        }
            .divertErrors(errors)
    val buttons =
        chooseCategoriesSharedVM.selectedCategories.map { it.isNotEmpty() }.asObservable2()
            .map { inSelectionMode ->
                listOfNotNull(
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Categorize all as Unknown",
                            isEnabled2 = isTransactionAvailable,
                            onClick = ::userCategorizeAllAsUnknown,
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
                            onClick = ::userRedo,
                        )
                    else null,
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Undo",
                            isEnabled2 = isUndoAvailable,
                            onClick = ::userUndo,
                        )
                    else null,
                    if (!inSelectionMode)
                        ButtonVMItem(
                            title = "Create Category",
                            onClick = navToNewCategory::onNext,
                        )
                    else null,
                    ButtonVMItem(
                        title = "Create Future",
                        onClick = ::userTryNavToCreateFuture2,
                    ),
                )
            }
}
