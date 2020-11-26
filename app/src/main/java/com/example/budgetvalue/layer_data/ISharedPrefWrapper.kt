package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.ReconcileCategoryAmount
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.DayOfWeek

interface ISharedPrefWrapper {
    fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmount>
    fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmount>?)
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
    fun fetchAnchorDateOffset(): Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?)
    fun fetchBlockSize(): Observable<Long>
    fun pushBlockSize(blockSize: Long?)
}