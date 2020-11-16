package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_data.IncomeCategoryAmounts

interface ISharedPrefWrapper {
    fun readIncomeCA(): List<IncomeCategoryAmounts>
    fun writeIncomeCA(incomeCA: List<IncomeCategoryAmounts>?)
}