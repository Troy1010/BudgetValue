package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.ActivePlanRepo
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.Category
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomainUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo,
) {
    suspend operator fun invoke(category: Category) {
        activePlanRepo.updateCategoryAmount(category, BigDecimal.ZERO)
        activeReconciliationRepo.pushCategoryAmount(category, null)
        categoriesRepo.delete(category)
    }
}