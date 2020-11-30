package com.example.budgetvalue.layer_data

import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IMyDaoWrapper: MyDao {
    val transactions: Observable<List<Transaction>>
    val planCategoryAmounts: Observable<Map<Category, BigDecimal>>
    fun pushPlanCategoryAmount(categoryAmount: Pair<Category, BigDecimal>): Completable
}