package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

class ReconciliationDomain @Inject constructor(
    private val reconciliationsRepo: IReconciliationsRepo,
) : IReconciliationDomain {
//    override fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?, ) =
//        reconciliation.categoryAmounts
//            .toMutableMap()
//            .apply { if (amount==null) remove(category) else put(category, amount) }
//            .let { reconciliationsRepo.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }) }

    override fun clearReconciliations() = reconciliationsRepo.clearReconciliations()

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliationsRepo.push(reconciliation)

    override fun delete(reconciliation: Reconciliation): Completable =
        reconciliationsRepo.delete(reconciliation)

    override val reconciliations: Observable<List<Reconciliation>> =
        reconciliationsRepo.reconciliations

    override val activeReconciliationCAs: Observable<Map<Category, BigDecimal>> =
        reconciliationsRepo.activeReconciliationCAs

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        reconciliationsRepo.pushActiveReconciliationCAs(categoryAmounts)

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        reconciliationsRepo.pushActiveReconciliationCA(kv)

    override fun clearActiveReconcileCAs(): Completable =
        reconciliationsRepo.clearActiveReconcileCAs()
}