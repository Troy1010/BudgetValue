package com.tminus1010.buva.ui.category_details

import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.replaceFirst
import com.tminus1010.buva.app.DeleteCategoryFromActiveDomain
import com.tminus1010.buva.app.ReplaceCategoryGlobally
import com.tminus1010.buva.data.CategoriesRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deleteCategoryFromActiveDomain: DeleteCategoryFromActiveDomain,
    private val categoriesRepo: CategoriesRepo,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
    private val showToast: ShowToast,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryName(s: String) {
        category.value = category.value!!.copy(name = s)
    }

    fun userSetCategoryDefaultAmountFormula(amountFormula: AmountFormula) {
        category.value = category.value!!.copy(defaultAmountFormula = amountFormula)
    }

    fun userSetCategoryType(categoryDisplayType: CategoryDisplayType) {
        category.value = category.value!!.withDisplayType(categoryDisplayType)
    }

    fun userDeleteCategory() {
        errors.globalScope.launch {
            deleteCategoryFromActiveDomain(category.value!!)
        }.use(throbberSharedVM)
        navigator.navUp()
    }

    fun userSubmit() {
        errors.globalScope.launch {
            if (category.value!!.name == ""
                || category.value!!.name.equals(Category.DEFAULT.name, ignoreCase = true)
                || category.value!!.name.equals(Category.UNRECOGNIZED.name, ignoreCase = true)
            ) {
                showToast("Invalid name")
            } else {
                if (originalCategory != null && originalCategory.name != category.value!!.name)
                    replaceCategoryGlobally(originalCategory, category.value!!)
                else
                    categoriesRepo.push(category.value!!)
                navigator.navUp()
            }
        }.use(throbberSharedVM)
    }

    fun userSetIsRememberedByDefault(b: Boolean) {
        category.value = category.value!!.copy(isRememberedByDefault = b)
    }

    fun userSetResetMax(x: BigDecimal?) {
        category.value = category.value!!.copy(resetStrategy = ResetStrategy.Basic(x))
    }

    fun userAddSearchText() {
        category.value = category.value!!.copy(onImportTransactionMatcher = category.value!!.onImportTransactionMatcher.withSearchText(""))
    }

    fun userAddSearchTotal() {
        category.value = category.value!!.copy(onImportTransactionMatcher = category.value!!.onImportTransactionMatcher.withSearchTotal(BigDecimal.ZERO))
    }

    fun userNavToChooseTransactionForTransactionMatcher(transactionMatcher: TransactionMatcher) {
        errors.globalScope.launch {
            val transaction = navigator.navToChooseTransaction()
            if (transaction != null)
                withContext(Dispatchers.Main) {
                    when (transactionMatcher) {
                        is TransactionMatcher.ByValue ->
                            category.value = category.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(category.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == transactionMatcher }, TransactionMatcher.ByValue(transaction.amount))))
                        is TransactionMatcher.SearchText ->
                            category.value = category.value!!.copy(onImportTransactionMatcher = TransactionMatcher.Multi(category.value!!.onImportTransactionMatcher.flattened().replaceFirst({ it == transactionMatcher }, TransactionMatcher.SearchText(transaction.description))))
                        else -> error("Unhandled type Z")
                    }
                }
        }
    }

    // # Internal
    private val originalCategory = savedStateHandle.get<Category>(KEY1)
    private val category = savedStateHandle.getLiveData<Category>(KEY1)

    // # Events
    val showDeleteConfirmationPopup = MutableSharedFlow<String>()

    // # State
    val title = flowOf("Category").shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val optionsTableView =
        combine(category.asFlow(), transactionMatcherPresentationFactory.viewModelItems(category.map { it.onImportTransactionMatcher }, { category.value = category.value?.copy(onImportTransactionMatcher = it) }, ::userNavToChooseTransactionForTransactionMatcher).asFlow())
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
                            onNewAmountFormula = ::userSetCategoryDefaultAmountFormula,
                        ),
                    ),
                    if (category.displayType == CategoryDisplayType.Reservoir)
                        listOf(
                            TextVMItem("Budget Reset Max"),
                            AmountPresentationModel(
                                bigDecimal = when (val x = category.resetStrategy) {
                                    is ResetStrategy.Basic -> x.budgetedMax
                                },
                                onNewAmount = ::userSetResetMax,
                            ),
                        )
                    else null,
                    listOf(
                        TextVMItem("Type"),
                        SpinnerVMItem(values = CategoryDisplayType.getPickableValues().toTypedArray(), initialValue = category.displayType, onNewItem = ::userSetCategoryType),
                    ),
                    listOf(
                        TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Is Remembered By Default"),
                        CheckboxVMItem(initialValue = category.isRememberedByDefault, onCheckChanged = ::userSetIsRememberedByDefault),
                    ),
                    *transactionMatcherVMItems.toTypedArray(),
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