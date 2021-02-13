package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_app.Plan
import com.tminus1010.budgetvalue.model_app.Reconciliation
import com.tminus1010.budgetvalue.model_app.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IMyDaoWrapper: MyDao {
    val transactions: Observable<List<Transaction>>
    val planCategoryAmounts: Observable<List<Plan>>
    fun pushPlanCategoryAmount(categoryAmount: Pair<Category, BigDecimal>): Completable
    fun pushPlanCAs(categoryAmounts: Map<Category, BigDecimal>)
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    fun fetchReconciliations(): Observable<List<Reconciliation>>
}