package com.tminus1010.budgetvalue.features.plans

import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

class PlanUseCasesImpl @Inject constructor(
    private val repo: Repo,
    private val categoryAmountsConverter: CategoryAmountsConverter,
): PlanUseCases {
    override val plans =
        repo.fetchPlans()
            .subscribeOn(Schedulers.io())
            .map { it.map { Plan.fromDTO(it, categoryAmountsConverter) } }
            .noEnd().replay(1).refCount()

    override fun pushPlan(plan: Plan) =
        repo.add(plan.toDTO(categoryAmountsConverter))
            .subscribeOn(Schedulers.io())

    override fun updatePlanCA(plan: Plan, categoryAmount: Pair<Category, BigDecimal?>): Completable {
        val amount = categoryAmount.second
        val category = categoryAmount.first
        return plan.categoryAmounts
            .toMutableMap()
            .apply { if (amount == null) remove(category) else put(category, amount) }
            .let {
                repo.updatePlanCategoryAmounts(
                    plan.toDTO(categoryAmountsConverter).startDate,
                    it.mapKeys { it.key.name })
            }
    }

    override fun updatePlanAmount(plan: Plan, amount: BigDecimal) =
        repo.updatePlanAmount(plan.toDTO(categoryAmountsConverter).startDate, amount)

    override fun delete(plan: Plan) =
        repo.delete(plan.toDTO(categoryAmountsConverter))
}