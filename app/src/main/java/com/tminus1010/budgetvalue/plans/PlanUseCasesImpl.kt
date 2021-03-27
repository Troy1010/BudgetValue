package com.tminus1010.budgetvalue.plans

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._core.data.RepoFacade
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class PlanUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade,
    private val categoryAmountsConverter: CategoryAmountsConverter,
): PlanUseCases {
    override val plans =
        repoFacade.fetchPlans()
            .subscribeOn(Schedulers.io())
            .map { it.map { Plan.fromDTO(it, categoryAmountsConverter) } }
            .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repoFacade.add(plan.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun updatePlanCA(plan: Plan, category: Category, amount: BigDecimal?): Completable =
        plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount == null) remove(category) else put(category, amount) }
            .let {
                repoFacade.updatePlanCategoryAmounts(
                    plan.toDTO(categoryAmountsConverter).startDate,
                    it.mapKeys { it.key.name })
            }

    override fun updatePlanAmount(plan: Plan, amount: BigDecimal) =
        repoFacade.updatePlanAmount(plan.toDTO(categoryAmountsConverter).startDate, amount)

    override fun delete(plan: Plan) =
        repoFacade.delete(plan.toDTO(categoryAmountsConverter))
}