package com.tminus1010.budgetvalue.features.plans

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.features_shared.Domain
import io.reactivex.rxjava3.subjects.PublishSubject

class PlansVM(
    private val domain: Domain
) : ViewModel() {
    val plans = domain.plans
    val intentDeletePlan = PublishSubject.create<Plan>()
        .also { it.launch { domain.delete(it) } }
}