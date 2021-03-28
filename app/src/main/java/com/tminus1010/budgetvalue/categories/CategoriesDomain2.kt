package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.plans.ActivePlanDomain
import com.tminus1010.budgetvalue.plans.PlanUseCases
import dagger.Reusable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@Reusable
class CategoriesDomain2 @Inject constructor(
    private val domainFacade: DomainFacade,
    private val planUseCases: PlanUseCases,
    private val activePlanDomain: ActivePlanDomain,
) : ViewModel(), ICategoriesDomain2 {
    override val intentDeleteCategoryFromActive = PublishSubject.create<Category>()
        .also {
            it.launch { category ->
                Rx.merge(
                    activePlanDomain.activePlan.flatMapCompletable { planUseCases.updatePlanCA(it, category, null) },
                    domainFacade.pushActiveReconciliationCA(Pair(category, null)),
                    domainFacade.delete(category),
                )
            }
        }
}