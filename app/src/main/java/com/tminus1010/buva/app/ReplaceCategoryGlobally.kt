package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.data.*
import com.tminus1010.buva.domain.Category
import javax.inject.Inject

class ReplaceCategoryGlobally @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val plansRepo: PlansRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo,
    private val transactionsInteractor: TransactionsInteractor,
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

        transactionsInteractor.push(
            transactionsInteractor.transactionsAggregate.value?.transactions?.map { it.copy(categoryAmounts = it.categoryAmounts.replaceKey(originalCategory, newCategory)) } ?: emptyList()
        )

        categoriesRepo.delete(originalCategory)
    }
}