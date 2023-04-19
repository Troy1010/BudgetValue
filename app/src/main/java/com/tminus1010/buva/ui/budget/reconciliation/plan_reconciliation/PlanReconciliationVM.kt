package com.tminus1010.buva.ui.budget.reconciliation.plan_reconciliation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.ActivePlanInteractor
import com.tminus1010.buva.app.ActivePlanReconciliationInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanReconciliationVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val userCategories: UserCategories,
    private val activePlanInteractor: ActivePlanInteractor,
    private val activePlanReconciliationInteractor: ActivePlanReconciliationInteractor,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userFillIntoCategory(category: Category) {
        GlobalScope.launch { activePlanReconciliationInteractor.fillIntoCategory(category) }
    }

    fun userViewTransactions(category: Category) {
        GlobalScope.launch { navigator.navToTransactions(reconciliationToDo.value!!.transactionBlock.transactions.filter { category in it.categoryAmounts.keys }) }
    }

    // # Private
    private val reconciliationToDo = savedStateHandle.getLiveData<ReconciliationToDo.PlanZ>(KEY1).asFlow().shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    // # State
    val subTitle = reconciliationToDo.map { it.transactionBlock.datePeriod!!.toDisplayStr() }
    val reconciliationTableView =
        combine(userCategories.flow, activePlanReconciliationInteractor.activeReconciliationCAsAndTotal, activePlanReconciliationInteractor.budgeted, reconciliationToDo, activePlanInteractor.activePlan)
        { categories, activeReconciliation, budgeted, reconciliationToDo, activePlan ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Actual"),
                        HeaderPresentationModel("Plan"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgeted.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Total"),
                        AmountPresentationModel(reconciliationToDo.transactionBlock.total),
                        AmountPresentationModel(activePlan.total),
                        AmountPresentationModel(activeReconciliation.total),
                        AmountPresentationModel(budgeted.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        AmountPresentationModel(reconciliationToDo.transactionBlock.defaultAmount),
                        AmountPresentationModel(activePlan.defaultAmount),
                        AmountPresentationModel(activeReconciliation.defaultAmount),
                        AmountPresentationModel(budgeted.defaultAmount, validation = { budgeted.defaultValidationResult }),
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            TextVMItem(
                                text1 = reconciliationToDo.transactionBlock.categoryAmounts[category]?.toString() ?: "",
                                menuVMItems = MenuVMItems(
                                    MenuVMItem(
                                        title = "View Transactions",
                                        onClick = { userViewTransactions(category) },
                                    ),
                                )
                            ),
                            AmountPresentationModel(activePlan.categoryAmounts[category]),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userUpdateActiveReconciliationCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userFillIntoCategory(category) }))),
                            AmountPresentationModel(bigDecimal = budgeted.categoryAmounts[category], validation = { budgeted.validation(category) }),
                        )
                    }.toTypedArray(),
                ),
                dividerMap = categories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.displayType })
                    .associate { it.index to it.value.displayType.name }
                    .mapKeys { it.key + 3 } // header row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}