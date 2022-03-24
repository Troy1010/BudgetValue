package com.tminus1010.budgetvalue._unrestructured.categories.domain

import com.tminus1010.budgetvalue.framework.Rx
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.data.ActivePlanRepo
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.ActiveReconciliationRepo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomainUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val activePlanRepo: ActivePlanRepo,
) {
    operator fun invoke(category: Category): Completable =
        Rx.merge(
            Rx.completableFromSuspend { activePlanRepo.updateCategoryAmount(category, BigDecimal.ZERO) },
            Rx.completableFromSuspend { activeReconciliationRepo.pushCategoryAmount(category, null) },
            Rx.completableFromSuspend { categoriesRepo.delete(category) },
        ).subscribeOn(Schedulers.io())
}