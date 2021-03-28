package com.tminus1010.budgetvalue.budgeted

import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

interface IBudgetedDomain {
    val categoryAmounts: Observable<Map<Category, BigDecimal>>
    val categoryAmountsObservableMap: Observable<Map<Category, BehaviorSubject<BigDecimal>>>
    val caTotal: Observable<BigDecimal>
    val defaultAmount: Observable<BigDecimal>
    val budgeted: BehaviorSubject<Budgeted>
}