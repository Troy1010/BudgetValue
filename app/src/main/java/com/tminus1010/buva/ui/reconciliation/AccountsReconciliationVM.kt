package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveReconciliationInteractor
import com.tminus1010.buva.app.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    userCategories: UserCategories,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    // # State
    val reconciliationTableView =
        combine(userCategories.flow, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal)
        { categories, activeReconciliation, budgetedWithActiveReconciliation ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(activeReconciliation.defaultAmount.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { budgetedWithActiveReconciliation.isDefaultAmountValid },
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userSetCategoryAmount),
                            AmountPresentationModel(budgetedWithActiveReconciliation.categoryAmounts[category]) { budgetedWithActiveReconciliation.isValid(category) },
                        )
                    }.toTypedArray(),
                ),
                dividerMap = categories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 2 } // header row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}