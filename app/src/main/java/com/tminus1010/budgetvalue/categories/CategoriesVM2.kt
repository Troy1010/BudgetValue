package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue._core.middleware.Rx
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

// Separate from CategoriesVM to avoid circular dependency graph
@HiltViewModel
class CategoriesVM2 @Inject constructor(
    private val domainFacade: DomainFacade,
    private val planUseCases: PlanUseCases,
    private val activePlanVM: ActivePlanVM,
): ViewModel() {
    val intentDeleteCategoryFromActive = PublishSubject.create<Category>()
        .also {
            it.launch { category ->
                Rx.merge(
                    activePlanVM.activePlan.flatMapCompletable { planUseCases.updatePlanCA(it, category, null) },
                    domainFacade.pushActiveReconciliationCA(Pair(category, null)),
                    domainFacade.delete(category),
                )
            }
        }
}