package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.all.presentation.models.ValidatedStringVMItem
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.service.ReconciliationPresentationMapper
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PlanReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    reconciliationPresentationMapper: ReconciliationPresentationMapper,
    budgetedInteractor: BudgetedInteractor,
    categoriesInteractor: CategoriesInteractor,
) : ViewModel() {
    // # View Events
    val reconciliationToDo = BehaviorSubject.create<ReconciliationToDo.PlanZ>()

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

    private val plan =
        reconciliationToDo
            .map { it.plan.categoryAmounts.mapValues { it.value.toString() } }

    private val actual =
        reconciliationToDo
            .map { it.transactionBlock.categoryAmounts.mapValues { it.value.toString() } }

    private val budgeted =
        budgetedInteractor.budgeted
            .map { it.categoryAmounts.mapValues { ValidatedStringVMItem(it.value) { BigDecimal.ZERO <= it } } }

    private val activeReconciliationUncategorizedAmount =
        reconciliationsRepo.activeReconciliationCAs.map { it.values.sum() }
            .map { ValidatedStringVMItem(it, BigDecimal::isZero) }

    // # State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationCAs, budgeted, budgetedInteractor.defaultAmount, activeReconciliationUncategorizedAmount, plan, actual)
        { categories, activeReconciliationCAs, budgeted, budgetedDefaultAmount, activeReconciliationUncategorizedAmount, plan, actual ->
            listOf(
                listOf(
                    listOf("Categories", "Plan", "Actual", "Reconcile", "Budgeted"),
                    listOf("Default", null, null, activeReconciliationUncategorizedAmount, budgetedDefaultAmount.toString())
                ),
                categories.map {
                    listOf(it.name, plan[it], actual[it], activeReconciliationCAs[it], budgeted[it])
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