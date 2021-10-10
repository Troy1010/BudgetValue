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
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    categoriesInteractor: CategoriesInteractor,
    budgetedWithActiveReconciliationInteractor: BudgetedWithActiveReconciliationInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(Pair(category, s.toMoneyBigDecimal())).subscribe()
    }

    // # Presentation State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedWithActiveReconciliationInteractor.categoryAmountsAndTotal)
        { categories, activeReconciliation, budgetedWithActiveReconciliation ->
            listOf(
                listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        "Default",
                        activeReconciliation.defaultAmount.toString(),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { it >= BigDecimal.ZERO },
                    ),
                ),
                categories.map {
                    listOf(
                        it.name,
                        CategoryAmountPresentationModel(it, activeReconciliation.categoryAmounts[it], ::userUpdateActiveReconciliationCategoryAmount),
                        AmountPresentationModel(budgetedWithActiveReconciliation.categoryAmounts[it]) { it >= BigDecimal.ZERO },
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