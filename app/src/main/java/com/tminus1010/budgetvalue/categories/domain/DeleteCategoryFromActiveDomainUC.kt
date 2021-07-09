package com.tminus1010.budgetvalue.categories.domain

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DeleteCategoryFromActiveDomainUC @Inject constructor(
    private val categoriesRepo: CategoriesRepo,
    private val reconciliationRepo: IReconciliationsRepo,
    private val plansRepo: IPlansRepo,
    private val activePlanDomain: ActivePlanDomain,
) : ViewModel() {
    operator fun invoke(category: Category) =
        Rx.merge(
            activePlanDomain.activePlan.take(1)
                .flatMapCompletable { plansRepo.updatePlanCA(it, category, null) },
            reconciliationRepo.pushActiveReconciliationCA(Pair(category, null)),
            categoriesRepo.delete(category),
        ).subscribeOn(Schedulers.io())
}