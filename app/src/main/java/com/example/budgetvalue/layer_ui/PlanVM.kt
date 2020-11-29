package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.total
import com.example.budgetvalue.layer_data.Repo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val planCategoryAmounts = repo.planCategoryAmounts
    val uncategorizedPlan = planCategoryAmounts
        .flatMap { it.observable }
        .map { it.values }
        .total()
        .replay(1).refCount()
    val expectedIncome = BehaviorSubject.createDefault(repo.fetchExpectedIncome())
        // # Bind expectedIncome -> Repo
        .also { it.skip(1).subscribe { repo.pushExpectedIncome(it) } }
    val difference = combineLatestAsTuple(expectedIncome, uncategorizedPlan)
        .map { it.first - it.second }
    init {
        // # Bind chooseableCategories -> planCA
        combineLatestAsTuple(categoriesAppVM.choosableCategories, planCategoryAmounts)
            .subscribeOn(Schedulers.io())
            .subscribe { (chooseableCategories, planCA) ->
                chooseableCategories.asSequence()
                    .filter { it !in planCA }
                    .forEach { planCA[it] = BigDecimal.ZERO }
            }
    }
}