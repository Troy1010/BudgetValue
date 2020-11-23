package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.pairwise
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesVM: CategoriesVM): ViewModel() {
    val planCategoryAmounts = SourceHashMap<String, BigDecimal>()
    private var repoLoadComplete = BehaviorSubject.createDefault(false) // TODO("Simplify this")
    val uncategorizedPlan = planCategoryAmounts.itemObservablesObservable
        .map { it.map { it.value } }
        .flatMap(::getTotalObservable)
        .toBehaviorSubject()
    fun getTotalObservable(it: Iterable<BehaviorSubject<BigDecimal>>): BehaviorSubject<BigDecimal> {
        val pairwiseDifference = BehaviorSubject.createDefault(BigDecimal.ZERO)
        val returning = pairwiseDifference
            .scan(BigDecimal.ZERO) { acc, y -> acc + y }
            .toBehaviorSubject() // TODO("Simplify")
        it.forEach { it.pairwise(BigDecimal.ZERO).map { it.second - it.first }.subscribe(pairwiseDifference) } // TODO("Are these subscriptions safe?")
        return returning
    }
    init {
        // # Bind once Repo <-> planCategoryAmounts
        // ## Bind once Repo -> planCategoryAmounts
        repo.getPlanCategoryAmounts().take(1).observeOn(Schedulers.io()).subscribe {
            planCategoryAmounts.putAll(it.associate { Pair(it.category, it.amount) })
            repoLoadComplete.onNext(true) // TODO("Simplify this")
        }
        // ## Bind planCategoryAmounts -> Repo
        combineLatestAsTuple(planCategoryAmounts.itemObservablesObservable, repoLoadComplete)
            .observeOn(Schedulers.io())
            .filter { it.second }
            .map { it.first }
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
        // # Bind categoriesVM.categories -> planCategoryAmounts
        combineLatestAsTuple(categoriesVM.categoryNames, repoLoadComplete)
            .observeOn(Schedulers.io())
            .filter { it.second }
            .map { it.first }
            .subscribe { categoryNames ->
                for (categoryName in planCategoryAmounts.keys) {
                    if (categoryName !in categoryNames) planCategoryAmounts.remove(categoryName)
                }
                for (categoryName in categoryNames) {
                    if (categoryName !in planCategoryAmounts)
                        planCategoryAmounts[categoryName] = BigDecimal.ZERO
                }
            }
    }
}