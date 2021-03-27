package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue._layer_facades.Domain
import com.tminus1010.budgetvalue._core.middleware.Rx
import io.reactivex.rxjava3.subjects.PublishSubject

// Separate from CategoriesVM to avoid circular dependency graph
class CategoriesVM2(
    private val domain: Domain,
    private val planUseCases: PlanUseCases,
    private val activePlanVM: ActivePlanVM,
): ViewModel() {
    val intentDeleteCategoryFromActive = PublishSubject.create<Category>()
        .also {
            it.launch { category ->
                Rx.merge(
                    activePlanVM.activePlan.flatMapCompletable { planUseCases.updatePlanCA(it, category, null) },
                    domain.pushActiveReconciliationCA(Pair(category, null)),
                    domain.delete(category),
                )
            }
        }
}