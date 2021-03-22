package com.tminus1010.budgetvalue.modules.reconciliations

import com.tminus1010.budgetvalue.modules.categories.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ReconciliationUseCases {
    val reconciliations: Observable<List<Reconciliation>>
    fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?): Completable
    fun clearReconciliations(): Completable
    val activeReconciliationCAs: Observable<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActiveReconcileCAs(): Completable
}