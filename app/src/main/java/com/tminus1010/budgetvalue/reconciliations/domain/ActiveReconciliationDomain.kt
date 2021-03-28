package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.Reusable
import java.math.BigDecimal
import javax.inject.Inject

@Reusable
class ActiveReconciliationDomain @Inject constructor(
    domainFacade: DomainFacade,
    categoriesDomain: CategoriesDomain
) : IActiveReconciliationDomain {
    override val activeReconcileCAs =
        Rx.combineLatest(domainFacade.activeReconciliationCAs, categoriesDomain.userCategories)
            .map { (activeReconcileCAs, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activeReconcileCAs
            }
            .toBehaviorSubject()
    override val activeReconcileCAs2 =
        activeReconcileCAs
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .replay(1).refCount()
}