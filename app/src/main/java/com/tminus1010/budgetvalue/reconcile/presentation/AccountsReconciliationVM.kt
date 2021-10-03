package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.service.ReconciliationPresentationMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    reconciliationPresentationMapper: ReconciliationPresentationMapper,
    budgetedInteractor: BudgetedInteractor,
    categoriesInteractor: CategoriesInteractor,
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(Pair(category, s.toMoneyBigDecimal())).subscribe()
    }

    // # Internal
    private val activeReconciliationCAs =
        Observable.combineLatest(reconciliationsRepo.activeReconciliationCAs, categoriesInteractor.userCategories)
        { activeReconciliationCAs, categories ->
            val map = categories.associateWith { BigDecimal.ZERO }
                .plus(activeReconciliationCAs)
            reconciliationPresentationMapper.getCategoryAmountVMItems(map, onDone = ::userUpdateActiveReconciliationCategoryAmount)
        }

    private val budgeted =
        budgetedInteractor.budgeted
            .map { it.categoryAmounts.mapValues { it.value.toString() } }

    // # State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationCAs, budgeted)
        { categories, activeReconciliationCAs, budgeted ->
            categories.map {
                listOf(it.name, activeReconciliationCAs[it], budgeted[it])
            }
        }
}