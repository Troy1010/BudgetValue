package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.ReconcileCategoryAmounts

interface ISharedPrefWrapper {
    fun fetchReconcileCategoryAmounts(): List<ReconcileCategoryAmounts>
    fun pushReconcileCategoryAmounts(reconcileCA: List<ReconcileCategoryAmounts>?)
}