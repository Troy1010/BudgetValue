package com.tminus1010.budgetvalue.categories.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomainUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationRepo: ReconciliationsRepo,
    private val plansRepo: PlansRepo,
    private val activePlanDomain: ActivePlanDomain,
) : ViewModel() {
    operator fun invoke(category: Category): Completable =
        Rx.merge(
            activePlanDomain.activePlan.take(1)
                .flatMapCompletable { plansRepo.updatePlanCA(it, category, null) },
            reconciliationRepo.pushActiveReconciliationCA(Pair(category, null)),
            categoriesRepo.delete(category),
        ).subscribeOn(Schedulers.io())
}