package com.tminus1010.budgetvalue.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class PlansVM @Inject constructor(
    private val domainFacade: DomainFacade
) : ViewModel() {
    val plans = domainFacade.plans
    val intentDeletePlan = PublishSubject.create<Plan>()
        .also { it.launch { domainFacade.delete(it) } }
}