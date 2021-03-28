package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.domain.IActiveReconciliationDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ActiveReconciliationVM @Inject constructor(
    domainFacade: DomainFacade,
    activeReconciliationDomain: ActiveReconciliationDomain,
) : ViewModel(), IActiveReconciliationDomain by activeReconciliationDomain {
    val intentPushActiveReconcileCA: PublishSubject<Pair<Category, BigDecimal>> = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.launch { domainFacade.pushActiveReconciliationCA(it) } }
}
