package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IReconciliationDomain {
    fun clearReconciliations(): Completable
    fun pushReconciliation(reconciliation: Reconciliation): Completable
    fun delete(reconciliation: Reconciliation): Completable
    val reconciliations: Observable<List<Reconciliation>>
    val activeReconciliationCAs: Observable<Map<Category, BigDecimal>>
    fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable
    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable
    fun clearActiveReconcileCAs(): Completable
}
