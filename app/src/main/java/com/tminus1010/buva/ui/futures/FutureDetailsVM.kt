package com.tminus1010.buva.ui.futures

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.*
import com.tminus1010.buva.app.CategorizeTransactions
import com.tminus1010.buva.app.CategoryAdapter
import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.all_layers.observable.source_objects.SourceHashMap
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionSharedVM
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.androidx.extensions.onNext
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class FutureDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryAdapter: CategoryAdapter,
    private val selectedCategoriesSharedVM: ChooseCategoriesSharedVM,
    private val futuresRepo: FuturesRepo,
    private val showToast: ShowToast,
    private val categorizeTransactions: CategorizeTransactions,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
    private val chooseTransactionSharedVM: ChooseTransactionSharedVM,
) : ViewModel() {
    // # User Intents
    fun userTryNavToChooseCategories() {
        navToChooseCategories.easyEmit()
    }

    fun userTryNavUp() {
        runBlocking { selectedCategoriesSharedVM.clearSelection() }
        navUp.onNext()
    }

    fun userDeleteFuture() {
        runBlocking { futuresRepo.delete(originalFuture) }
        userTryNavUp()
    }

    @SuppressLint("VisibleForTests")
    fun userTrySubmit() {
        try {
            if (future.value!!.name == "") throw InvalidNameException()
            if (future.value!!.fillCategory == Category.UNRECOGNIZED) throw InvalidFillCategoryException()
            runBlocking {
                futuresRepo.push(future.value!!)
                if (future.value!!.terminationStrategy == TerminationStrategy.PERMANENT)
                    categorizeTransactions({ future.value!!.onImportTransactionMatcher?.isMatch(it) ?: false }, future.value!!::categorize)
                        .also { showToast(NativeText.Simple("$it transactions categorized")) }
                if (future.value!!.name != originalFuture.name) futuresRepo.delete(originalFuture)
                userTryNavUp()
            }
        } catch (e: Throwable) {
            when (e) {
                is InvalidFillCategoryException -> showToast(NativeText.Simple("Invalid fill category"))
                is InvalidNameException -> showToast(NativeText.Simple("Invalid name"))
                else -> throw e
            }
        }
    }

    fun userSetIsOnlyOnce(b: Boolean) {
        future.onNext(future.value!!.copy(terminationStrategy = if (b) TerminationStrategy.ONCE else TerminationStrategy.PERMANENT))
    }

    fun userSetName(s: String) {
        future.onNext(future.value!!.copy(name = s))
    }

    private val userCategoryAmountFormulas = SourceHashMap<Category, AmountFormula>()
    fun userSetCategoryAmountFormula(category: Category, amountFormula: AmountFormula) {
        if (amountFormula.isZero())
            userCategoryAmountFormulas.remove(category)
        else
            userCategoryAmountFormulas[category] = amountFormula
    }

    private val userSetFillCategory = MutableSharedFlow<Category>()
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoryAdapter.parseCategory(categoryName))
    }

    fun userNavToChooseTransactionForTransactionMatcher(transactionMatcher: TransactionMatcher) {
        lastSelectedTransactionMather = transactionMatcher
        navToChooseTransaction.onNext()
    }

    fun userSetTotalGuess(s: String) {
        future.onNext(future.value!!.copy(totalGuess = s.toMoneyBigDecimal()))
    }

    fun userAddSearchText() {
        future.value = future.value!!.copy(onImportTransactionMatcher = future.value!!.onImportTransactionMatcher.withSearchText(""))
    }

    fun userAddSearchTotal() {
        future.value = future.value!!.copy(onImportTransactionMatcher = future.value!!.onImportTransactionMatcher.withSearchTotal(BigDecimal.ZERO))
    }

    // # Internal
    private val originalFuture = savedStateHandle.get<Future>(KEY1)!!
    private val future = savedStateHandle.getLiveData<Future>(KEY1)

    private var lastSelectedTransactionMather: TransactionMatcher? = null
    private val categoryAmountFormulas =
        combine(userCategoryAmountFormulas.flow, selectedCategoriesSharedVM.selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(originalFuture.categoryAmountFormulas)
                .plus(userCategoryAmountFormulas.filter { it.key in selectedCategories })
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryAmountFormulas())
            .apply { observe(viewModelScope) { future.onNext(future.value!!.copy(categoryAmountFormulas = it)) } }
    private val fillCategory =
        selectedCategoriesSharedVM.selectedCategories
            .flatMapLatest { userSetFillCategory.onStart { emit(it.find { it.defaultAmountFormula.isZero() } ?: it.getOrNull(0) ?: Category.UNRECOGNIZED) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Category.UNRECOGNIZED)
            .apply { observe(viewModelScope) { future.onNext(future.value!!.copy(fillCategory = it)) } }
    private val fillAmountFormula =
        combine(categoryAmountFormulas, fillCategory, future.map { it.totalGuess }.asFlow())
        { categoryAmountFormulas, fillCategory, total ->
            categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]
                ?: AmountFormula.Value(BigDecimal.ZERO)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AmountFormula.Value(BigDecimal.ZERO))

    init {
        chooseTransactionSharedVM.userSubmitTransaction.observe(viewModelScope) {
            when (lastSelectedTransactionMather) {
                is TransactionMatcher.ByValue ->
                    future.value = future.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(future.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.ByValue(it.amount))))
                is TransactionMatcher.SearchText ->
                    future.value = future.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(future.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.SearchText(it.description))))
                else -> error("Unhandled type Z")
            }
        }
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val navToChooseCategories = MutableSharedFlow<Unit>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val optionsTableView =
        combine(future.asFlow(), transactionMatcherPresentationFactory.viewModelItems(future.map { it.onImportTransactionMatcher }, { future.value = future.value?.copy(onImportTransactionMatcher = it) }, ::userNavToChooseTransactionForTransactionMatcher).asFlow())
        { future, transactionMatcherVMItems ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextVMItem("Name"),
                        EditTextVMItem(text = future.name, onDone = ::userSetName),
                    ),
                    listOf(
                        TextVMItem("Default Amount"),
                        EditTextVMItem(textFlow = this.future.map { it.totalGuess.toString() }.asFlow(), onDone = ::userSetTotalGuess)
                    ),
                    listOf(
                        TextVMItem("Is Only Once"),
                        CheckboxVMItem(initialValue = future.terminationStrategy == TerminationStrategy.ONCE, onCheckChanged = ::userSetIsOnlyOnce)
                    ),
                    *transactionMatcherVMItems.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
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
                    title = "Delete",
                    onClick = ::userDeleteFuture,
                ),
                ButtonVMItem(
                    title = "Add Or Remove Categories",
                    onClick = ::userTryNavToChooseCategories,
                ),
                ButtonVMItem(
                    title = "Add Search Total",
                    onClick = ::userAddSearchTotal,
                ),
                ButtonVMItem(
                    title = "Add Search Text",
                    onClick = ::userAddSearchText,
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userTrySubmit,
                ),
            )
        )
}