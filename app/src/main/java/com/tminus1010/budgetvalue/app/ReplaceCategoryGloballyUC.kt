package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.data.ActivePlanRepo
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.data.PlansRepo
import com.tminus1010.budgetvalue.domain.Category
import javax.inject.Inject

class ReplaceCategoryGloballyUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val plansRepo: PlansRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo,
) {
    suspend operator fun invoke(originalCategory: Category, newCategory: Category) {
        categoriesRepo.push(newCategory)
        activePlanRepo.pushCategoryAmounts(activePlanRepo.activePlan.value.categoryAmounts.replaceKey(originalCategory, newCategory))
        activeReconciliationRepo.pushCategoryAmounts(activeReconciliationRepo.activeReconciliationCAs.value.replaceKey(originalCategory, newCategory))

        reconciliationsRepo.reconciliations.value?.forEach {
            reconciliationsRepo.push(it.copy(categoryAmounts = it.categoryAmounts.replaceKey(originalCategory, newCategory)))
        }

        plansRepo.plans.value?.forEach {
            plansRepo.push(it.copy(categoryAmounts = it.categoryAmounts.replaceKey(originalCategory, newCategory)))
        }

        categoriesRepo.delete(originalCategory)
    }
}