package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.pairwise
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesVM: CategoriesVM): ViewModel() {
    val planCategoryAmounts = SourceHashMap<String, BigDecimal>()
    val loadFromRepoObservable = repo.getPlanCategoryAmounts()
        .observeOn(Schedulers.io())
        .take(1)
        .doOnNext { planCategoryAmounts.putAll(it.associate { Pair(it.category, it.amount) }) }
        .toBehaviorSubject()
    val uncategorizedPlan = planCategoryAmounts.itemObservablesObservable
        .map { it.map { it.value } }
        .flatMap(::getTotalObservable)
        .toBehaviorSubject()
    init {
        // # Bind planCategoryAmounts -> Repo
        loadFromRepoObservable
            .observeOn(Schedulers.io())
            .switchMap { planCategoryAmounts.itemObservablesObservable }
            .doOnNext { Completable.fromAction { repo.clearPlanCategoryAmounts() }.blockingAwait() }
            .subscribe {
                for ((categoryName, amountBehaviorSubject) in it) {
                    repo.addPlanCategoryAmounts(PlanCategoryAmounts(
                        categoryName,
                        BigDecimal.ZERO
                    ))
                    amountBehaviorSubject.observeOn(Schedulers.io()).subscribe { // TODO("Handle disposables")
                        repo.updatePlanCategoryAmounts(PlanCategoryAmounts(
                            categoryName,
                            it
                        ))
                    }
                }
            }
        // # Bind categoriesVM.categoryNames -> planCategoryAmounts
        loadFromRepoObservable
            .observeOn(Schedulers.io())
            .switchMap { combineLatestAsTuple(planCategoryAmounts.observable, categoriesVM.categoryNames) }
            .subscribe { (planCategoryAmounts, categoryNames) ->
                for (categoryName in planCategoryAmounts.keys) {
                    if (categoryName !in categoryNames)
                        planCategoryAmounts.remove(categoryName)
                }
                for (categoryName in categoryNames) {
                    if (categoryName !in planCategoryAmounts)
                        planCategoryAmounts[categoryName] = BigDecimal.ZERO
                }
            }
    }
    fun getTotalObservable(it: Iterable<BehaviorSubject<BigDecimal>>): BehaviorSubject<BigDecimal> {
        val pairwiseDifference = BehaviorSubject.createDefault(BigDecimal.ZERO)
        val returning = pairwiseDifference
            .scan(BigDecimal.ZERO) { acc, y -> acc + y }
            .toBehaviorSubject() // TODO("Simplify")
        it.forEach { it.pairwise(BigDecimal.ZERO).map { it.second - it.first }.subscribe(pairwiseDifference) } // TODO("Are these subscriptions safe?")
        return returning
    }
}