package com.tminus1010.buva.ui.futures

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.extensions.*
import com.tminus1010.buva.app.CategorizeTransactions
import com.tminus1010.buva.environment.CategoryAdapter
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.all_layers.observable.source_objects.SourceHashMap
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionSharedVM
import com.tminus1010.buva.ui.receipt_categorization.ReceiptCategorizationSharedVM
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFutureVM @Inject constructor(
    private val categoryAdapter: CategoryAdapter,
    private val selectedCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val futuresRepo: FuturesRepo,
    private val showToast: ShowToast,
    private val categorizeTransactions: CategorizeTransactions,
    private val transactionsInteractor: TransactionsInteractor,
    private val receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
    private val chooseTransactionSharedVM: ChooseTransactionSharedVM,
) : ViewModel() {
    // # Setup
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun userTryNavToCategorySelection() {
        navToCategorySelection.easyEmit()
    }

    fun userTryNavToReceiptCategorization() {
        navToReceiptCategorization.easyEmit(Pair("New Future", totalGuess.value))
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        GlobalScope.launch {
            showAlertDialog.value!!(
                body = NativeText.Simple("What would you like to name this future?"),
                initialText = categoryAmountFormulas.value
                    .map { (category, amountFormula) ->
                        if (category != fillCategory.value)
                            amountFormula.toDisplayStr2() + " " + category.name
                        else
                            category.name
                    }
                    .joinToString(", "),
                onSubmitText = {
                    try {
                        val futureToPush =
                            Future(
                                name = it.toString().ifEmpty { throw InvalidNameException() },
                                categoryAmountFormulas = categoryAmountFormulas.value,
                                fillCategory = fillCategory.value ?: throw InvalidFillCategoryException(),
                                terminationStrategy = if (userSetIsOnlyOnce.value) TerminationStrategy.ONCE else TerminationStrategy.PERMANENT,
                                terminationDate = null,
                                isAvailableForManual = true,
                                onImportTransactionMatcher = onImportTransactionMatcher.value!!,
                                totalGuess = totalGuess.value,
                            )
                        runBlocking {
                            futuresRepo.push(futureToPush)
                            if (futureToPush.terminationStrategy == TerminationStrategy.PERMANENT)
                                categorizeTransactions({ futureToPush.onImportTransactionMatcher?.isMatch(it) ?: false }, futureToPush::categorize)
                                    .also { showToast(NativeText.Simple("$it transactions categorized")) }
                            selectedCategoriesSharedVM.clearSelection()
                            navUp.emit(Unit)
                        }
                    } catch (e: Throwable) {
                        when (e) {
                            is NoDescriptionEnteredException -> showToast(NativeText.Simple("Fill description or use another search type"))
                            is InvalidFillCategoryException -> showToast(NativeText.Simple("Invalid fill category"))
                            is InvalidNameException -> showToast(NativeText.Simple("Invalid name"))
                            else -> throw e
                        }
                    }
                }
            )
        }
    }

    private var onImportTransactionMatcher: MutableLiveData<TransactionMatcher> = MutableLiveData(TransactionMatcher.Multi())
    fun userAddSearchText() {
        onImportTransactionMatcher.value = onImportTransactionMatcher.value.withSearchText("")
    }

    fun userAddSearchTotal() {
        onImportTransactionMatcher.value = onImportTransactionMatcher.value.withSearchTotal(BigDecimal.ZERO)
    }

    // TODO: Simplify by having futureToPush instead of each individual thing
    private val totalGuess = MutableStateFlow(transactionsInteractor.mostRecentUncategorizedSpend.value?.amount ?: BigDecimal("-10"))
    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    private val userSetIsOnlyOnce = MutableStateFlow(false)
    fun userSetIsOnlyOnce(b: Boolean) {
        userSetIsOnlyOnce.onNext(b)
    }

    private val userCategoryAmountFormulas = SourceHashMap<Category, AmountFormula>()
    fun userSetCategoryAmountFormula(category: Category, amountFormula: AmountFormula) {
        if (amountFormula.isZero())
            userCategoryAmountFormulas.remove(category)
        else
            userCategoryAmountFormulas[category] = amountFormula
    }

    private val userSetFillCategory = MutableSharedFlow<Category?>()
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoryAdapter.parseCategory(categoryName))
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesSharedVM.clearSelection() }
        navUp.onNext()
    }

    fun userNavToChooseTransactionForTransactionMatcher(transactionMatcher: TransactionMatcher) {
        lastSelectedTransactionMather = transactionMatcher
        navToChooseTransaction.onNext()
    }

    // # Internal
    init {
        receiptCategorizationSharedVM.userSubmitCategorization.observe(viewModelScope) {
            selectedCategoriesSharedVM.selectCategories(*it.keys.toTypedArray())
            userCategoryAmountFormulas.adjustTo(it.mapValues { AmountFormula.Value(it.value) })
        }
        chooseTransactionSharedVM.userSubmitTransaction.observe(viewModelScope) {
            onImportTransactionMatcher.value =
                when (lastSelectedTransactionMather) {
                    is TransactionMatcher.ByValue ->
                        TransactionMatcher.Multi(onImportTransactionMatcher.value!!.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.ByValue(it.amount)))
                    is TransactionMatcher.SearchText ->
                        TransactionMatcher.Multi(onImportTransactionMatcher.value!!.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.SearchText(it.description)))
                    else -> error("Unhandled type Z")
                }
        }
    }

    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesSharedVM.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())
    private val fillCategory =
        selectedCategoriesSharedVM.selectedCategories
            .flatMapLatest { userSetFillCategory.onStart { emit(it.find { it.defaultAmountFormula.isZero() } ?: it.getOrNull(0)) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            fillCategory
                ?.let { categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory] }
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value(BigDecimal.ZERO))
    private var lastSelectedTransactionMather: TransactionMatcher? = null


    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToCategorySelection = MutableSharedFlow<Unit>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToReceiptCategorization = MutableSharedFlow<Pair<String, BigDecimal>>()

    // # State
    val optionsTableView =
        transactionMatcherPresentationFactory.viewModelItems(onImportTransactionMatcher, { onImportTransactionMatcher.value = it }, ::userNavToChooseTransactionForTransactionMatcher)
            .map { transactionMatcherVMItems ->
                TableViewVMItem(
                    recipeGrid = listOfNotNull(
                        listOf(
                            TextVMItem("Is Only Once"),
                            CheckboxVMItem(initialValue = userSetIsOnlyOnce.value, onCheckChanged = ::userSetIsOnlyOnce)
                        ),
                        *transactionMatcherVMItems.toTypedArray()
                    ),
                    shouldFitItemWidthsInsideTable = true,
                )
            }
            .asFlow()
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val categoryAmountsTableView =
        combine(categoryAmountFormulas.flatMapSourceHashMap { it.itemFlowMap }, fillCategory)
        { categoryAmountFormulaItemFlows, fillCategory ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf<IHasToViewItemRecipe>(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                    ),
                    *categoryAmountFormulaItemFlows.map { (category, amountFormula) ->
                        CategoryAmountFormulaPresentationModel(category, fillCategory, if (category == fillCategory) fillAmountFormula else amountFormula, { userSetFillCategory(it.name) }, { userSetCategoryAmountFormula(category, it) }).toHasToViewItemRecipes()
                    }.toTypedArray(),
                ),
                dividerMap = categoryAmountFormulaItemFlows.keys.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 1 } // header row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Add Or Remove Categories",
                    onClick = ::userTryNavToCategorySelection,
                ),
                ButtonVMItem(
                    title = "Receipt Categorization",
                    onClick = ::userTryNavToReceiptCategorization,
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userTrySubmit,
                ),
            )
        )
}