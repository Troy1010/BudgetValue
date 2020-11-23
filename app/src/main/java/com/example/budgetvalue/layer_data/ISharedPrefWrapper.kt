package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
import java.math.BigDecimal

interface ISharedPrefWrapper {
    fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmounts>
    fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmounts>?)
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?)
}