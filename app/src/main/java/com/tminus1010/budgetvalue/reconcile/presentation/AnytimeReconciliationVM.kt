package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ValidatedStringVMItem
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.budgetvalue.reconcile.presentation.service.ReconciliationPresentationFactory
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AnytimeReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    reconciliationPresentationFactory: ReconciliationPresentationFactory,
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
            reconciliationPresentationFactory.getCategoryAmountVMItems(map, onDone = ::userUpdateActiveReconciliationCategoryAmount)
        }

    private val budgetedCAs =
        budgetedInteractor.budgetedWithActiveReconciliation
            .map { it.categoryAmounts.mapValues { ValidatedStringVMItem(it.value) { BigDecimal.ZERO <= it } } }

    private val activeReconciliationUncategorizedAmount =
        reconciliationsRepo.activeReconciliationCAs.map { it.values.sum() }
            .map { ValidatedStringVMItem(it, BigDecimal::isZero) }

    // # State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationCAs, budgetedCAs, budgetedInteractor.budgetedWithActiveReconciliation, activeReconciliationUncategorizedAmount)
        { categories, activeReconciliationCAs, budgeted, budgetedWithActiveReconciliation, activeReconciliationUncategorizedAmount ->
            listOf(
                listOf(
                    listOf(HeaderPresentationModel("Categories"), HeaderPresentationModel("Reconcile"), HeaderPresentationModel("Budgeted")),
                    listOf("Default", activeReconciliationUncategorizedAmount, budgetedWithActiveReconciliation.defaultAmount.toString())
                ),
                categories.map {
                    listOf(it.name, activeReconciliationCAs[it], budgeted[it])
                }
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