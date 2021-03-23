package com.tminus1010.budgetvalue.features.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.features.plans.ActivePlanVM
import com.tminus1010.budgetvalue.features.plans.PlanUseCases
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.middleware.Rx
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoryDeletionVM(
    private val domain: Domain,
    private val planUseCases: PlanUseCases,
    private val activePlanVM: ActivePlanVM,
): ViewModel() {
    val intentDeleteCategoryFromActive = PublishSubject.create<Category>()
        .also {
            it.launch { category ->
                Rx.merge(
                    activePlanVM.activePlan.flatMapCompletable { planUseCases.updatePlanCA(it, Pair(category, null)) },
                    domain.pushActiveReconciliationCA(Pair(category, null)),
                    domain.delete(category),
                )
            }
        }
}