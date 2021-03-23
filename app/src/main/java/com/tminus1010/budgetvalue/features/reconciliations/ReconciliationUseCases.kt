package com.tminus1010.budgetvalue.features.reconciliations

import com.tminus1010.budgetvalue.features.categories.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ReconciliationUseCases {
    val reconciliations: Observable<List<Reconciliation>>
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?): Completable
    fun clearReconciliations(): Completable
    val activeReconciliationCAs: Observable<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActiveReconcileCAs(): Completable
}