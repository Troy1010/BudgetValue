package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.getTotalObservable
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.PlanCategoryAmount
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val planCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    val loadFromRepoObservable = repo.planCategoryAmounts
        .observeOn(Schedulers.io())
        .take(1)
        .doOnNext { planCategoryAmounts.putAll(it) }
        .toBehaviorSubject()
    val uncategorizedPlan = planCategoryAmounts.itemObservablesObservable
        .flatMap { getTotalObservable(it.values) }
        .toBehaviorSubject()
    val expectedIncome = BehaviorSubject.createDefault(repo.fetchExpectedIncome())
        // # Bind expectedIncome -> Repo
        .also { it.skip(1).subscribe { repo.pushExpectedIncome(it) } }
    val difference = combineLatestAsTuple(expectedIncome, uncategorizedPlan)
        .map { it.first - it.second }

    init {
        // # Bind categoriesAppVM.categoryNames -> planCategoryAmounts
        loadFromRepoObservable
            .observeOn(Schedulers.io())
            .switchMap { categoriesAppVM.choosableCategories }
            .subscribe { chooseableCategories ->
                synchronized(planCategoryAmounts) {
                    for (category in planCategoryAmounts.keys.filter { it !in chooseableCategories }) {
                        planCategoryAmounts.remove(category)
                    }
                    for (category in chooseableCategories.filter { it !in planCategoryAmounts }) {
                        planCategoryAmounts[category] = BigDecimal.ZERO
                    }
                }
            }
    }
}