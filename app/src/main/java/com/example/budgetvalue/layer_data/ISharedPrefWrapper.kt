package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.ReconcileCategoryAmount
import java.math.BigDecimal

interface ISharedPrefWrapper {
    fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmount>
    fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmount>?)
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
    fun fetchAnchorDateOffset(): Int
    fun pushAnchorDateOffset(anchorDateOffset: Int?)
}