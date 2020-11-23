package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.getTotalObservable
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesVM: CategoriesVM): ViewModel() {
    val planCategoryAmounts = SourceHashMap<String, BigDecimal>()
    val loadFromRepoObservable = repo.getPlanCategoryAmounts()
        .observeOn(Schedulers.io())
        .take(1)
        .doOnNext { planCategoryAmounts.putAll(it.associate { Pair(it.category, it.amount) }) }
        .toBehaviorSubject()
    val uncategorizedPlan = planCategoryAmounts.itemObservablesObservable
        .flatMap { getTotalObservable(it.values) }
        .toBehaviorSubject()
    init {
        // # Bind planCategoryAmounts -> Repo
        loadFromRepoObservable
            .observeOn(Schedulers.io())
            .switchMap { planCategoryAmounts.itemObservablesObservable }
            .subscribe {
                repo.clearPlanCategoryAmounts().blockingAwait()
                synchronized(planCategoryAmounts) {
                    for ((categoryName, amountBehaviorSubject) in it) {
                        repo.add(PlanCategoryAmounts(categoryName, BigDecimal.ZERO))
                        amountBehaviorSubject.observeOn(Schedulers.io())
                            .subscribe { // TODO("Handle disposables")
                                repo.update(PlanCategoryAmounts(categoryName, it))
                            }
                    }
                }
            }
        // # Bind categoriesVM.categoryNames -> planCategoryAmounts
        loadFromRepoObservable
            .observeOn(Schedulers.io())
            .switchMap { categoriesVM.choosableCategoryNames }
            .subscribe { categoryNames ->
                synchronized(planCategoryAmounts) {
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
    }
}