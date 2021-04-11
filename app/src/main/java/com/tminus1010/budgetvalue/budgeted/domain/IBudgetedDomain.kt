package com.tminus1010.budgetvalue.budgeted.domain

import com.tminus1010.budgetvalue.budgeted.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

interface IBudgetedDomain {
    val categoryAmounts: Observable<Map<Category, BigDecimal>>
    val categoryAmountsObservableMap: Observable<Map<Category, BehaviorSubject<BigDecimal>>>
    val defaultAmount: Observable<BigDecimal>
    val budgeted: BehaviorSubject<Budgeted>
}