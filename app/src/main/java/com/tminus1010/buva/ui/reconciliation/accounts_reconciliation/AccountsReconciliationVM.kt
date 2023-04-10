package com.tminus1010.buva.ui.reconciliation.accounts_reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveReconciliationInteractor
import com.tminus1010.buva.app.BudgetedForActiveReconciliationInteractor
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
    budgetedForActiveReconciliationInteractor: BudgetedForActiveReconciliationInteractor,
    private val activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    userCategories: UserCategories,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userDumpIntoCategory(category: Category) {
        GlobalScope.launch {
            activeReconciliationRepo.pushCategoryAmount(
                category = category,
                activeReconciliationRepo.activeReconciliationCAs.first().calcFillAmount(
                    fillCategory = category,
                    total = activeReconciliationInteractor.activeReconciliationCAsAndTotal.first().total,
                ),
            )
        }
    }

    // # State
    val reconciliationTableView =
        combine(userCategories.flow, activeReconciliationInteractor.activeReconciliationCAsAndTotal, budgetedForActiveReconciliationInteractor.categoryAmountsAndTotal)
        { categories, activeReconciliation, budgetedWithActiveReconciliation ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Total"),
                        TextVMItem(activeReconciliation.total.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(activeReconciliation.defaultAmount.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { budgetedWithActiveReconciliation.isDefaultAmountValid },
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userSetCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userDumpIntoCategory(category) }))),
                            AmountPresentationModel(budgetedWithActiveReconciliation.categoryAmounts[category]) { budgetedWithActiveReconciliation.isValid(category) },
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