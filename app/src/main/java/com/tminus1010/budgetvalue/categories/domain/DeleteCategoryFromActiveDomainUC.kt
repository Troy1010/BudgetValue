package com.tminus1010.budgetvalue.categories.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.asObservable2
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo3
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomainUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationRepo: ReconciliationsRepo,
    private val plansRepo: PlansRepo,
    private val activePlanRepo: ActivePlanRepo3,
) : ViewModel() {
    operator fun invoke(category: Category): Completable =
        Rx.merge(
            activePlanRepo.activePlan.asObservable2().take(1)
                .flatMapCompletable { Rx.completableFromSuspend { plansRepo.updatePlanCategoryAmount(it, category, BigDecimal.ZERO) } },
            reconciliationRepo.pushActiveReconciliationCA(Pair(category, null)),
            Rx.completableFromSuspend { categoriesRepo.delete(category) },
        ).subscribeOn(Schedulers.io())
}