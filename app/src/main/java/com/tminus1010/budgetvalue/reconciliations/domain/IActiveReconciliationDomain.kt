package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

interface IActiveReconciliationDomain {
    val activeReconcileCAs: BehaviorSubject<Map<Category, BigDecimal>>
    val activeReconcileCAs2: Observable<Map<Category, BehaviorSubject<BigDecimal>>>
}