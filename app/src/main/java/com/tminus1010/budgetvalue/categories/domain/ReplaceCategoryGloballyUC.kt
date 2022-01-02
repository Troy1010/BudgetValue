package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo3
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import javax.inject.Inject

class ReplaceCategoryGloballyUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val plansRepo: PlansRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo3,
) {
    suspend fun replaceCategoryGlobally(originalCategory: Category, newCategory: Category) {
        categoriesRepo.push(newCategory)
        activePlanRepo.pushCategoryAmounts(activePlanRepo.activePlan.value.categoryAmounts.replaceKey(originalCategory, newCategory))
        activeReconciliationRepo.pushCategoryAmounts(activeReconciliationRepo.activeReconciliationCAs.value.replaceKey(originalCategory, newCategory))

        reconciliationsRepo.reconciliations.blockingFirst().forEach {
            reconciliationsRepo.push(it.copy(categoryAmounts = it.categoryAmounts.replaceKey(originalCategory, newCategory))).blockingAwait()
        }

        plansRepo.plans.value?.forEach {
            plansRepo.push(it.copy(categoryAmounts = it.categoryAmounts.replaceKey(originalCategory, newCategory)))
        }

        categoriesRepo.delete(originalCategory)
    }
}