package com.tminus1010.buva.app

import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.domain.Category
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomain @Inject constructor(
    private val categoryRepo: CategoryRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo,
) {
    suspend operator fun invoke(category: Category) {
        activePlanRepo.updateCategoryAmount(category, BigDecimal.ZERO)
        activeReconciliationRepo.pushCategoryAmount(category, null)
        categoryRepo.delete(category)
    }
}