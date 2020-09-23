package com.example.budgetvalue.layer_data

import com.example.budgetvalue.models.IncomeCategoryAmounts

interface ISharedPrefWrapper {
    fun readIncomeCA(): List<IncomeCategoryAmounts>
    fun writeIncomeCA(incomeCA: List<IncomeCategoryAmounts>?)
}