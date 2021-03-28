package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.extensions.launch
import dagger.Reusable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@Reusable
class PlansDomain @Inject constructor(
    private val domainFacade: DomainFacade
) : ViewModel(), IPlansDomain {
    override val plans = domainFacade.plans
    override val intentDeletePlan = PublishSubject.create<Plan>()
        .also { it.launch { domainFacade.delete(it) } }
}