package com.tminus1010.budgetvalue.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.app.ActiveReconciliationInteractor
import com.tminus1010.budgetvalue.app.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.ui.all_features.model.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

@HiltViewModel
class PlanReconciliationVM @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    categoriesInteractor: CategoriesInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
) : ViewModel() {
    // # Setup
    val reconciliationToDo = BehaviorSubject.create<ReconciliationToDo.PlanZ>()

    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    // # State
    val reconciliationTableView =
        combine(categoriesInteractor.userCategories, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal, reconciliationToDo.asFlow())
        { categories, activeReconciliation, budgetedWithActiveReconciliation, reconciliationToDo ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Actual"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(reconciliationToDo.transactionBlock.defaultAmount.toString()),
                        TextVMItem(activeReconciliation.defaultAmount.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { budgetedWithActiveReconciliation.isDefaultAmountValid },
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            TextVMItem(reconciliationToDo.transactionBlock.categoryAmounts[category]?.toString() ?: ""),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userUpdateActiveReconciliationCategoryAmount),
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