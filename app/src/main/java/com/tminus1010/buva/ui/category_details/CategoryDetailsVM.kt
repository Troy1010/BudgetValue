package com.tminus1010.buva.ui.category_details

import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.replaceFirst
import com.tminus1010.buva.app.DeleteCategoryFromActiveDomain
import com.tminus1010.buva.app.ReplaceCategoryGlobally
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.environment.android_wrapper.ActivityWrapper
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deleteCategoryFromActiveDomain: DeleteCategoryFromActiveDomain,
    private val categoryRepo: CategoryRepo,
    private val replaceCategoryGlobally: ReplaceCategoryGlobally,
    private val errors: Errors,
    private val throbberSharedVM: ThrobberSharedVM,
    private val transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
    private val showToast: ShowToast,
    private val navigator: Navigator,
    private val activityWrapper: ActivityWrapper,
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

    fun userTryDeleteCategory() {
        GlobalScope.launch {
            activityWrapper.showAlertDialog(
                body = NativeText.Simple("Are you sure you want to delete these categories?\n\t${category.value!!.name}"),
                onYes = {
                    errors.globalScope.launch {
                        deleteCategoryFromActiveDomain(category.value!!)
                    }.use(throbberSharedVM)
                    navigator.navUp()
                },
                onNo = { }
            )
        }
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
                    categoryRepo.push(category.value!!)
                navigator.navUp()
            }
        }.use(throbberSharedVM)
    }

    fun userSetIsRememberedByDefault(b: Boolean) {
        category.value = category.value!!.copy(isRememberedWithEditByDefault = b)
    }

    fun userSetResetMax(x: BigDecimal?) {
        when (val reconciliationStrategyGroup = category.value!!.reconciliationStrategyGroup) {
            is ReconciliationStrategyGroup.Always ->
                category.value = category.value!!.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(resetStrategy = ResetStrategy.Basic(x)))
            is ReconciliationStrategyGroup.Reservoir ->
                category.value = category.value!!.copy(reconciliationStrategyGroup = reconciliationStrategyGroup.copy(resetStrategy = ResetStrategy.Basic(x)))
        }
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

    // # Private
    private val originalCategory = savedStateHandle.get<Category>(KEY1)
    private val category = savedStateHandle.getLiveData<Category>(KEY1)
    private val transactionMatcherVMItems =
        transactionMatcherPresentationFactory.viewModelItems(
            transactionMatcher = category.map { it.onImportTransactionMatcher },
            onChange = { category.value = category.value?.copy(onImportTransactionMatcher = it) },
            userNavToChooseTransactionForTransactionMatcher = ::userNavToChooseTransactionForTransactionMatcher,
        ).asFlow()

    // # State
    val title = flowOf("Category").shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val optionsTableView =
        combine(category.asFlow(), transactionMatcherVMItems)
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
                    when (category.displayType) {
                        CategoryDisplayType.Reservoir ->
                            listOf(
                                TextVMItem("Budget Reset Max"),
                                AmountPresentationModel(
                                    bigDecimal = when (val x = category.reconciliationStrategyGroup.resetStrategy) {
                                        is ResetStrategy.Basic -> x.budgetedMax
                                        null -> null
                                    },
                                    onNewAmount = ::userSetResetMax,
                                ),
                            )
                        else -> null
                    },
                    listOf(
                        TextVMItem("Type"),
                        SpinnerVMItem(values = CategoryDisplayType.getPickableValues().toTypedArray(), initialValue = category.displayType, onNewItem = ::userSetCategoryType),
                    ),
                    listOf(
                        TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Is Remembered By Default"),
                        CheckboxVMItem(initialValue = category.isRememberedWithEditByDefault, onCheckChanged = ::userSetIsRememberedByDefault),
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
                    onClick = { userTryDeleteCategory() }
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