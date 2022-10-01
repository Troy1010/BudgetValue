package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveReconciliationInteractor
import com.tminus1010.buva.app.ActiveReconciliationInteractor2
import com.tminus1010.buva.app.BudgetedForActiveReconciliationInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ReconciliationToDo
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
    savedStateHandle: SavedStateHandle,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    userCategories: UserCategories,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    budgetedForActiveReconciliationInteractor: BudgetedForActiveReconciliationInteractor,
    private val activeReconciliationInteractor2: ActiveReconciliationInteractor2,
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userFillIntoCategory(category: Category) {
        GlobalScope.launch { activeReconciliationInteractor2.fillIntoCategory(category) }
    }

    // # Internal
    private val reconciliationToDo = savedStateHandle.getLiveData<ReconciliationToDo.PlanZ>(KEY1).asFlow()

    // # State
    val subTitle = reconciliationToDo.map { it.transactionBlock.datePeriod!!.toDisplayStr() }
    val reconciliationTableView =
        combine(userCategories.flow, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedForActiveReconciliationInteractor.categoryAmountsAndTotal, reconciliationToDo, activeReconciliationInteractor.targetDefaultAmount)
        { categories, activeReconciliation, budgetedForActiveReconciliation, reconciliationToDo, targetDefaultAmount ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Actual"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedForActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Total"),
                        TextVMItem(reconciliationToDo.transactionBlock.total.toString()),
                        TextVMItem(activeReconciliation.total.toString()),
                        AmountPresentationModel(budgetedForActiveReconciliation.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(reconciliationToDo.transactionBlock.defaultAmount.toString()),
                        AmountPresentationModel(activeReconciliation.defaultAmount, checkIfValid = { activeReconciliation.defaultAmount == targetDefaultAmount }),
                        AmountPresentationModel(budgetedForActiveReconciliation.defaultAmount, checkIfValid = { budgetedForActiveReconciliation.isDefaultAmountValid })
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            TextVMItem(reconciliationToDo.transactionBlock.categoryAmounts[category]?.toString() ?: ""),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userUpdateActiveReconciliationCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userFillIntoCategory(category) }))),
                            AmountPresentationModel(budgetedForActiveReconciliation.categoryAmounts[category]) { budgetedForActiveReconciliation.isValid(category) },
                        )
                    }.toTypedArray(),
                ),
                dividerMap = categories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 3 } // header row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}