package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

interface ISharedPrefWrapper {
    val activeReconciliationCAs: BehaviorSubject<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>?)
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>)
    fun clearActiveReconcileCAs()
    val activePlanCAs: Observable<Map<Category, BigDecimal>>
    fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>?)
    fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>)
    fun clearActivePlan()
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
    val anchorDateOffset: Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?)
    val blockSize: Observable<Long>
    fun pushBlockSize(blockSize: Long?)
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(boolean: Boolean = true)
}