package com.tminus1010.budgetvalue.reconciliations

import com.tminus1010.budgetvalue._core.data.RepoFacade
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

class ReconciliationUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    private val categoryParser: ICategoryParser
): ReconciliationUseCases {

    override fun pushReconciliationCA(reconciliation: Reconciliation, category: Category, amount: BigDecimal?, ) =
        reconciliation.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repoFacade.updateReconciliationCategoryAmounts(reconciliation.id, it.mapKeys { it.key.name }) }

    override fun clearReconciliations() = repoFacade.clearReconciliations()

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        repoFacade.add(reconciliation.toDTO(categoryAmountsConverter))

    override fun delete(reconciliation: Reconciliation): Completable =
        repoFacade.delete(reconciliation.toDTO(categoryAmountsConverter))

    override val reconciliations: Observable<List<Reconciliation>> =
        repoFacade.fetchReconciliations()
            .map { it.map { Reconciliation.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()

    override val activeReconciliationCAs: Observable<Map<Category, BigDecimal>> =
        repoFacade.activeReconciliationCAs
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() } }

    override fun pushActiveReconciliationCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        repoFacade.pushActiveReconciliationCAs(categoryAmounts.associate { it.key.name to it.value.toString() })

    override fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        repoFacade.pushActiveReconciliationCA(Pair(kv.first.name, kv.second?.toString()))

    override fun clearActiveReconcileCAs(): Completable =
        repoFacade.clearActiveReconcileCAs()
}