package com.tminus1010.buva.ui.budget.reconciliation.accounts_reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveAccountsReconciliationInteractor
import com.tminus1010.buva.app.BudgetedInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    private val activeAccountsReconciliationInteractor: ActiveAccountsReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val userCategories: UserCategories,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userDumpIntoCategory(category: Category) {
        GlobalScope.launch {
            activeReconciliationRepo.pushCategoryAmount(
                category = category,
                amount = activeReconciliationRepo.activeReconciliationCAs.first().calcFillAmount(
                    fillCategory = category,
                    total = activeAccountsReconciliationInteractor.activeReconciliationCAsAndTotal.first().total,
                ),
            )
        }
    }

    // # State
    val reconciliationTableView =
        combine(userCategories.flow, activeAccountsReconciliationInteractor.activeReconciliationCAsAndTotal, activeAccountsReconciliationInteractor.budgeted)
        { categories, activeReconciliation, budgeted ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgeted.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Total"),
                        TextVMItem(activeReconciliation.total.toString()),
                        AmountPresentationModel(budgeted.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(activeReconciliation.defaultAmount.toString()),
                        AmountPresentationModel(budgeted.defaultAmount, checkIfValid = { budgeted.isDefaultAmountValid }),
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userSetCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userDumpIntoCategory(category) }))),
                            AmountPresentationModel(budgeted.categoryAmounts[category], checkIfValid = { budgeted.isValid(category) }), // TODO:Make this checkIfValid similar to PlanReconciliationVM's
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