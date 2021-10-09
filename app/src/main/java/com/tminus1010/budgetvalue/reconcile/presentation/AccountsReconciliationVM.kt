package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ValidatedStringVMItem
import com.tminus1010.budgetvalue.budgeted.BudgetedInteractor
import com.tminus1010.budgetvalue.budgeted.presentation.BudgetHeaderPresentationModel
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.app.ReconciliationToDo
import com.tminus1010.budgetvalue.reconcile.app.convenience_service.ReconciliationsToDo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.budgetvalue.reconcile.presentation.service.ReconciliationPresentationFactory
import com.tminus1010.tmcommonkotlin.core.logx
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    reconciliationPresentationFactory: ReconciliationPresentationFactory,
    budgetedInteractor: BudgetedInteractor,
    categoriesInteractor: CategoriesInteractor,
    reconciliationsToDo: ReconciliationsToDo
) : ViewModel() {
    // # User Intents
    fun userUpdateActiveReconciliationCategoryAmount(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(Pair(category, s.toMoneyBigDecimal())).subscribe()
    }

    // # Internal
    private val reconciliationToDo =
        reconciliationsToDo
            .map { it.find { it is ReconciliationToDo.Accounts }!! as ReconciliationToDo.Accounts }
            .toSingle().cache()
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
        Observable.combineLatest(reconciliationsRepo.activeReconciliationCAs, reconciliationToDo.toObservable())
        { activeReconciliationCAs, reconciliationToDo ->
            reconciliationToDo.difference - activeReconciliationCAs.values.sum()
        }
            .map { ValidatedStringVMItem(it, BigDecimal::isZero) }

    // # Presentation State
    val recipeGrid =
        Observable.combineLatest(categoriesInteractor.userCategories, activeReconciliationCAs, budgetedCAs, budgetedInteractor.budgetedWithActiveReconciliation, activeReconciliationUncategorizedAmount)
        { categories, activeReconciliationCAs, budgetedCAs, budgeted, activeReconciliationUncategorizedAmount ->
            listOf(
                listOf(
                    listOf(HeaderPresentationModel("Categories"), HeaderPresentationModel("Reconcile"), BudgetHeaderPresentationModel("Budgeted", budgeted.totalAmount.toString()) ),
                    listOf("Default", activeReconciliationUncategorizedAmount, budgeted.defaultAmount.logx("qqq(defaultAmount").toString())
                ),
                categories.map {
                    listOf(it.name, activeReconciliationCAs[it], budgetedCAs[it])
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