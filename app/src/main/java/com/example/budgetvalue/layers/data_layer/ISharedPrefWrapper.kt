package com.example.budgetvalue.layers.data_layer

import com.example.budgetvalue.models.IncomeCategoryAmounts

interface ISharedPrefWrapper {
    fun readIncomeCA(): List<IncomeCategoryAmounts>
    fun writeIncomeCA(incomeCA: List<IncomeCategoryAmounts>?)
}