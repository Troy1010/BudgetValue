package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.SourceHashMap
import com.tminus1010.budgetvalue.model_app.Category
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ISharedPrefWrapper {
    val reconcileCategoryAmounts: Observable<SourceHashMap<Category, BigDecimal>>
    fun pushReconcileCategoryAmounts(reconcileCategoryAmounts: Map<Category, BigDecimal>?)
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
    fun fetchAnchorDateOffset(): Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?)
    fun fetchBlockSize(): Observable<Long>
    fun pushBlockSize(blockSize: Long?)
}