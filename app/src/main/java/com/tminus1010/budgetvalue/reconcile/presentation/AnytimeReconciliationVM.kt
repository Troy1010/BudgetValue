package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all.presentation_and_view._models.ValidatedStringVMItem
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.service.ReconciliationPresentationMapper
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AnytimeReconciliationVM @Inject constructor(
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
            .map { it.categoryAmounts.mapValues { ValidatedStringVMItem(it.value) { BigDecimal.ZERO <= it } } }

    private val activeReconciliationUncategorizedAmount =
        reconciliationsRepo.activeReconciliationCAs.map { it.values.sum() }
            .map { ValidatedStringVMItem(it, BigDecimal::isZero) }

    // # State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationCAs, budgeted, budgetedInteractor.defaultAmount, activeReconciliationUncategorizedAmount)
        { categories, activeReconciliationCAs, budgeted, budgetedDefaultAmount, activeReconciliationUncategorizedAmount ->
            listOf(
                listOf(
                    listOf("Categories", "Reconcile", "Budgeted"),
                    listOf("Default", activeReconciliationUncategorizedAmount, budgetedDefaultAmount.toString())
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