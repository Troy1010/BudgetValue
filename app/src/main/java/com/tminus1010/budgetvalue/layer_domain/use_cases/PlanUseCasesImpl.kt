package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.CategoryAmountsConverter
import com.tminus1010.budgetvalue.layer_domain.ICategoryParser
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.model_domain.Reconciliation
import com.tminus1010.tmcommonkotlin.misc.extensions.associate
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class PlanUseCasesImpl @Inject constructor(
    private val repo: Repo,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    val categoryParser: ICategoryParser,
): PlanUseCases {
    override val plans =
        repo.fetchPlans()
            .subscribeOn(Schedulers.io())
            .map { it.map { Plan.fromDTO(it, categoryAmountsConverter) } }
            .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repo.add(plan.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun pushPlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount==null) remove(category) else put(category, amount) }
            .let { repo.updatePlanCategoryAmounts(plan.toDTO(categoryAmountsConverter).startDate, it.mapKeys { it.key.name }) }

    override fun pushReconciliation(reconciliation: Reconciliation): Completable =
        reconciliation.toDTO(categoryAmountsConverter)
            .let { repo.add(it).subscribeOn(Schedulers.io()) }

    override val activePlanCAs: Observable<Map<Category, BigDecimal>> =
        repo.activePlanCAs
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimal() } }

    override fun pushActivePlanCAs(categoryAmounts: Map<Category, BigDecimal>): Completable =
        repo.pushActivePlanCAs(categoryAmounts.associate { it.key.name to it.value.toString() })

    override fun pushActivePlanCA(kv: Pair<Category, BigDecimal?>): Completable =
        repo.pushActivePlanCA(Pair(kv.first.name, kv.second?.toString()))

    override fun clearActivePlan(): Completable =
        repo.clearActivePlanCAs()
}