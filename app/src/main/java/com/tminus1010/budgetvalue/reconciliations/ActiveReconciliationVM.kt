package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ActiveReconciliationVM @Inject constructor(
    private val domainFacade: DomainFacade,
    categoriesVM: CategoriesVM,
) : ViewModel() {
    val intentPushActiveReconcileCA: PublishSubject<Pair<Category, BigDecimal>> = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.launch { domainFacade.pushActiveReconciliationCA(it) } }
    val activeReconcileCAs =
        Rx.combineLatest(domainFacade.activeReconciliationCAs, categoriesVM.userCategories)
            .map { (activeReconcileCAs, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activeReconcileCAs
            }
            .toBehaviorSubject()
    val activeReconcileCAs2 =
        activeReconcileCAs
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .replay(1).refCount()
}
