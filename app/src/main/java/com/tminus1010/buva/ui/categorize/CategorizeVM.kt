package com.tminus1010.buva.ui.categorize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.*
import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.TransactionMatcher
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem2
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.buva.ui.set_string.SetStringSharedVM
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.takeUntilSignal
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
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
    private val userCategories: UserCategories,
    private val throbberSharedVM: ThrobberSharedVM,
    private val chooseCategoriesSharedVM: ChooseCategoriesSharedVM,
    errors: Errors,
    futuresRepo: FuturesRepo,
    private val futuresInteractor: FuturesInteractor,
    private val redoUndoInteractor: RedoUndoInteractor,
    private val setStringSharedVM: SetStringSharedVM,
    private val categorizeTransactions: CategorizeTransactions,
    private val categoryInteractor: CategoryInteractor,
    private val configInteractor: ConfigInteractor,
    private val deleteCategoryFromActiveDomain: DeleteCategoryFromActiveDomain
) : ViewModel() {
    // # User Intents
    fun userSimpleCategorize(category: Category) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            transactionsInteractor.push(
                transactionsInteractor.mostRecentUncategorizedSpend.value!!.categorize(category)
            )
        })
    }

    fun userReplay(future: Future) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            transactionsInteractor.push(
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
            val categoryUnknown = userCategories.flow.first().find { it.name.equals("Unknown", ignoreCase = true) }!! // TODO: Handle this error
            transactionsInteractor.push(
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
        navToSetString.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseDescription(future: Future) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            categorizeTransactions(TransactionMatcher.SearchText(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)::isMatch, future::categorize)
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userUseDescriptionWithEdit(future: Future) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                categorizeTransactions(TransactionMatcher.SearchText(s)::isMatch, future::categorize)
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToSetString.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseDescriptionOnCategory(category: Category) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            categorizeTransactions(TransactionMatcher.SearchText(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)::isMatch, categorize = { it.categorize(category) })
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userUseDescriptionWithEditOnCategory(category: Category) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                categorizeTransactions(TransactionMatcher.SearchText(s)::isMatch, categorize = { it.categorize(category) })
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToSetString.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userUseAndRememberDescriptionOnCategory(category: Category) {
        GlobalScope.launch(block = throbberSharedVM.decorate {
            categoryInteractor.addDescriptionAndCategorize(
                category = category,
                description = transactionsInteractor.mostRecentUncategorizedSpend.value!!.description,
            )
                .also { showToast(NativeText.Simple("$it transactions categorized")) }
        })
    }

    fun userUseAndRememberDescriptionWithEditOnCategory(category: Category) {
        setStringSharedVM.userSubmitString.take(1).takeUntilSignal(setStringSharedVM.userCancel).observe(GlobalScope) { s ->
            GlobalScope.launch(block = throbberSharedVM.decorate { // TODO: There should be a better way than launching within a launch, right?
                categoryInteractor.addDescriptionAndCategorize(
                    category = category,
                    description = s,
                )
                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
            })
        }
        navToSetString.onNext(transactionsInteractor.mostRecentUncategorizedSpend.value!!.description)
    }

    fun userDeleteCategory(category: Category) {
        GlobalScope.launch {
            deleteCategoryFromActiveDomain(category)
        }.use(throbberSharedVM)
    }

    fun userTryNavToCategorySettings(category: Category) {
        navToCategoryDetails.easyEmit(category)
    }

    fun userTryNavToReceiptCategorization() {
        navToReceiptCategorization.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend.value!!)
    }

    fun userTryNavToReceiptCategorizationImageToText() {
        navToReceiptCategorizationImageToText.easyEmit(transactionsInteractor.mostRecentUncategorizedSpend.value!!)
    }

    // # Events
    val navToCreateFuture = MutableSharedFlow<Unit>()
    val navToCategoryDetails = MutableSharedFlow<Category>()
    val navToNewCategory = MutableSharedFlow<Unit>()
    val navToReplayOrFutureDetails = MutableSharedFlow<Future>()
    val navToReceiptCategorization = MutableSharedFlow<Transaction>()
    val navToReceiptCategorizationImageToText = MutableSharedFlow<Transaction>()
    val navToSetString = MutableSharedFlow<String>()

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
    val items =
        combine(futuresRepo.futures.map { it.filter { it.isAvailableForManual } }, userCategories.flow)
        { nonAutomaticFutures, userCategories ->
            listOf(
                *userCategories.map { category ->
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
                                MenuVMItem(
                                    title = "Use and Remember",
                                    onClick = { userUseAndRememberDescriptionOnCategory(category) }
                                ),
                                MenuVMItem(
                                    title = "Use and Remember with Edit",
                                    onClick = { userUseAndRememberDescriptionWithEditOnCategory(category) }
                                ),
                                MenuVMItem(
                                    title = "Delete",
                                    onClick = { userDeleteCategory(category) }
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
        combine(chooseCategoriesSharedVM.selectedCategories.map { it.isNotEmpty() }, configInteractor.config)
        { inSelectionMode, config ->
            listOfNotNull(
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Categorize all as Unknown",
                        isEnabled2 = isTransactionAvailable,
                        onClick = ::userCategorizeAllAsUnknown,
                    )
                else null,
                if (!inSelectionMode && config.isImageToTextEnabled)
                    ButtonVMItem(
                        title = "Do Receipt Categorization ImageToText",
                        isEnabled2 = isTransactionAvailable,
                        onClick = ::userTryNavToReceiptCategorizationImageToText,
                    )
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Do Receipt Categorization",
                        isEnabled2 = isTransactionAvailable,
                        onClick = ::userTryNavToReceiptCategorization,
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
