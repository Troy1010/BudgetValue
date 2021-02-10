package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_app.Category
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ISharedPrefWrapper {
    fun fetchActiveReconcileCAs(): Map<Category, BigDecimal>
    fun pushActiveReconcileCAs(categoryAmounts: Map<Category, BigDecimal>?)
    fun pushActiveReconcileCA(kv: Pair<Category, BigDecimal?>)
    fun fetchActivePlanCAs(): Map<Category, BigDecimal>
    fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>?)
    fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>)
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
    fun fetchAnchorDateOffset(): Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?)
    fun fetchBlockSize(): Observable<Long>
    fun pushBlockSize(blockSize: Long?)
}