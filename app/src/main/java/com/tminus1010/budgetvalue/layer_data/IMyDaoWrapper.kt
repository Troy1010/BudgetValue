package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate

interface IMyDaoWrapper: MyDao {
    val transactions: Observable<List<Transaction>>
    val planCategoryAmounts: Observable<Map<Category, BigDecimal>>
    fun pushPlanCategoryAmount(categoryAmount: Pair<Category, BigDecimal>): Completable
    fun pushReconciliation(reconciliation: Reconciliation): Completable
}