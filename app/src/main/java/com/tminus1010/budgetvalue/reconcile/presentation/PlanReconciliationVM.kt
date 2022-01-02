package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.AmountPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountPresentationModel
import com.tminus1010.budgetvalue.budgeted.presentation.BudgetHeaderPresentationModel
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.app.interactor.ActiveReconciliationInteractor
import com.tminus1010.budgetvalue.reconcile.app.interactor.BudgetedWithActiveReconciliationInteractor
import com.tminus1010.budgetvalue.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanReconciliationVM @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    categoriesInteractor: CategoriesInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
) : ViewModel() {
    // # View Events
    val reconciliationToDo = BehaviorSubject.create<ReconciliationToDo.PlanZ>()

    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        GlobalScope.launch { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    // # Presentation State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal, reconciliationToDo)
        { categories, activeReconciliation, budgetedWithActiveReconciliation, reconciliationToDo ->
            listOf(
                listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Actual"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        "Default",
                        reconciliationToDo.transactionBlock.defaultAmount.toString(),
                        activeReconciliation.defaultAmount.toString(),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { budgetedWithActiveReconciliation.isDefaultAmountValid },
                    ),
                ),
                categories.map { category ->
                    listOf(
                        category.name,
                        reconciliationToDo.transactionBlock.categoryAmounts[category]?.toString() ?: "",
                        CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userUpdateActiveReconciliationCategoryAmount),
                        AmountPresentationModel(budgetedWithActiveReconciliation.categoryAmounts[category]) { budgetedWithActiveReconciliation.isValid(category) },
                    )
                },
            ).flatten()
        }
    val dividerMap =
        categoriesInteractor.userCategories
            .map {
                it.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 2 } // header row, default row
            }
}