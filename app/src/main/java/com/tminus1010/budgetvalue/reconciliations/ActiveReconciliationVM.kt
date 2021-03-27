package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.aa_core.middleware.Rx
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.aa_shared.Domain
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.aa_core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class ActiveReconciliationVM(
    private val domain: Domain,
    categoriesVM: CategoriesVM,
) : ViewModel() {
    val intentPushActiveReconcileCA: PublishSubject<Pair<Category, BigDecimal>> = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.launch { domain.pushActiveReconciliationCA(it) } }
    val activeReconcileCAs =
        Rx.combineLatest(domain.activeReconciliationCAs, categoriesVM.userCategories)
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
