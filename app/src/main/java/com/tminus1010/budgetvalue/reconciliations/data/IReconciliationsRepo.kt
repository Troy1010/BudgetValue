package com.tminus1010.budgetvalue.reconciliations.data

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IReconciliationsRepo {
    val reconciliations: Observable<List<Reconciliation>>
    fun push(reconciliation: Reconciliation): Completable
    fun delete(reconciliation: Reconciliation): Completable
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?): Completable
    fun clearReconciliations(): Completable
    val activeReconciliationCAs: Observable<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActiveReconcileCAs(): Completable
}