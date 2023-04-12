package com.tminus1010.buva.ui.budget.reconciliation.plan_reconciliation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveAccountsReconciliationInteractor
import com.tminus1010.buva.app.ActivePlanInteractor
import com.tminus1010.buva.app.ActivePlanReconciliationInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.domain.ResolutionStrategy
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanReconciliationVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val userCategories: UserCategories,
    private val activeAccountsReconciliationInteractor: ActiveAccountsReconciliationInteractor,
    private val activePlanInteractor: ActivePlanInteractor,
    private val activePlanReconciliationInteractor: ActivePlanReconciliationInteractor,
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userFillIntoCategory(category: Category) {
        GlobalScope.launch { activeAccountsReconciliationInteractor.fillIntoCategory(category) }
    }

    // # Private
    private val reconciliationToDo = savedStateHandle.getLiveData<ReconciliationToDo.PlanZ>(KEY1).asFlow()

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
                        AmountPresentationModel(budgeted.defaultAmount, checkIfValid = { budgeted.isDefaultAmountValid }),
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            TextVMItem(reconciliationToDo.transactionBlock.categoryAmounts[category]?.toString() ?: ""),
                            AmountPresentationModel(activePlan.categoryAmounts[category]),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userUpdateActiveReconciliationCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userFillIntoCategory(category) }))),
                            AmountPresentationModel(
                                bigDecimal = budgeted.categoryAmounts[category],
                                checkIfValid = {
                                    when (val x = category.reconciliationStrategyGroup.planResolutionStrategy) {
                                        is ResolutionStrategy.MatchPlan -> x.isValid(category, activeReconciliation.categoryAmounts, budgeted.categoryAmounts, activePlan.categoryAmounts)
                                        is ResolutionStrategy.Basic -> x.isValid(category, budgeted.categoryAmounts)
                                    }
                                },
                            ),
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