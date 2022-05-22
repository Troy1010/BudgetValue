package com.tminus1010.buva.ui.category_details

import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.InvalidCategoryNameException
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.*
import com.tminus1010.buva.app.DeleteCategoryFromActiveDomain
import com.tminus1010.buva.app.ReplaceCategoryGlobally
import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionSharedVM
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CategoryDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deleteCategoryFromActiveDomain: DeleteCategoryFromActiveDomain,
    private val categoriesRepo: CategoriesRepo,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val chooseTransactionSharedVM: ChooseTransactionSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryName(s: String) {
        category.value = category.value!!.copy(name = s)
    }

    fun userSetCategoryDefaultAmountFormula(amountFormula: AmountFormula) {
        category.value = category.value!!.copy(defaultAmountFormula = amountFormula)
    }

    fun userSetCategoryType(categoryType: CategoryType) {
        category.value = category.value!!.copy(type = categoryType)
    }

    fun userDeleteCategory() {
        errors.globalScope.launch {
            deleteCategoryFromActiveDomain(category.value!!)
        }.use(throbberSharedVM)
        navUp.easyEmit(Unit)
    }

    fun userSubmit() {
        errors.globalScope.launch(errors) {
            if (category.value!!.name == ""
                || category.value!!.name.equals(Category.DEFAULT.name, ignoreCase = true)
                || category.value!!.name.equals(Category.UNRECOGNIZED.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
            if (originalCategory != null && originalCategory.name != category.value!!.name)
                replaceCategoryGlobally(originalCategory, category.value!!)
            else
                categoriesRepo.push(category.value!!)
            navUp.easyEmit(Unit)
        }.use(throbberSharedVM)
    }

    fun userSetIsRememberedByDefault(b: Boolean) {
        category.value = category.value!!.copy(isRememberedByDefault = b)
    }

    fun userAddSearchText() {
        category.value = category.value!!.copy(onImportTransactionMatcher = category.value!!.onImportTransactionMatcher.withSearchText(""))
    }

    fun userAddSearchTotal() {
        category.value = category.value!!.copy(onImportTransactionMatcher = category.value!!.onImportTransactionMatcher.withSearchTotal(BigDecimal.ZERO))
    }

    fun userNavToChooseTransactionForTransactionMatcher(transactionMatcher: TransactionMatcher) {
        lastSelectedTransactionMather = transactionMatcher
        navToChooseTransaction.onNext()
    }

    // # Internal
    private val originalCategory = savedStateHandle.get<Category>(KEY1)
    private val category = savedStateHandle.getLiveData<Category>(KEY1)
    private var lastSelectedTransactionMather: TransactionMatcher? = null

    init {
        chooseTransactionSharedVM.userSubmitTransaction.observe(viewModelScope) {
            when (lastSelectedTransactionMather) {
                is TransactionMatcher.ByValue ->
                    category.value = category.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(category.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.ByValue(it.amount))))
                is TransactionMatcher.SearchText ->
                    category.value = category.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(category.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == lastSelectedTransactionMather }, TransactionMatcher.SearchText(it.description))))
                else -> error("Unhandled type Z")
            }
        }
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()
    val showDeleteConfirmationPopup = MutableSharedFlow<String>()
    val navToChooseTransaction = MutableSharedFlow<Unit>()

    // # State
    val title = flowOf("Category").shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val optionsTableView =
        combine(category.asFlow(), transactionMatcherPresentationFactory.viewModelItems(category.map { it.onImportTransactionMatcher }, { category.value = category.value?.copy(onImportTransactionMatcher = it) }, ::userNavToChooseTransactionForTransactionMatcher).asFlow(), )
        { category, transactionMatcherVMItems ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextVMItem("Name"),
                        EditTextVMItem(text = category.name, onDone = ::userSetCategoryName),
                    ),
                    listOf(
                        TextVMItem("Default Amount"),
                        AmountFormulaPresentationModel1(
                            amountFormula = this.category.map { it.defaultAmountFormula },
                            onNewAmountFormula = ::userSetCategoryDefaultAmountFormula
                        ),
                    ),
                    listOf(
                        TextVMItem("Type"),
                        SpinnerVMItem(values = CategoryType.getPickableValues().toTypedArray(), initialValue = category.type, onNewItem = ::userSetCategoryType),
                    ),
                    listOf(
                        TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Is Remembered By Default"),
                        CheckboxVMItem(initialValue = category.isRememberedByDefault, onCheckChanged = ::userSetIsRememberedByDefault),
                    ),
                    *transactionMatcherVMItems.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Delete",
                    onClick = { showDeleteConfirmationPopup.easyEmit(category.value!!.name) }
                ),
                ButtonVMItem(
                    title = "Done",
                    onClick = ::userSubmit
                ),
                ButtonVMItem(
                    title = "Add Search Total",
                    onClick = ::userAddSearchTotal,
                ),
                ButtonVMItem(
                    title = "Add Search Text",
                    onClick = ::userAddSearchText,
                ),
            )
        )
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}