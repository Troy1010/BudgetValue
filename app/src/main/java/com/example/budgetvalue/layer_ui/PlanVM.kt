package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesVM: CategoriesVM): ViewModel() {
    val planCategoryAmounts = SourceHashMap<String, BehaviorSubject<BigDecimal>>()
    init {
        // # Bind categoriesVM.categories -> planCategoryAmounts
        categoriesVM.categories.observeOn(Schedulers.io()).subscribe { categories ->
            for ((categoryName, amountObservable) in planCategoryAmounts) {
                if (categoryName !in categories.map { it.name })
                    planCategoryAmounts.remove(categoryName)
            }
            for (categoryName in categories.map { it.name }) {
                if (categoryName !in planCategoryAmounts)
                    planCategoryAmounts[categoryName] = BehaviorSubject.createDefault(BigDecimal.ZERO)
            }
        }
        // # Sync planCategoryAmounts with Repo
        // ## Bind once Repo -> planCategoryAmounts
        repo.getPlanCategoryAmounts().take(1).observeOn(Schedulers.io()).subscribe {
            logz("Repo -> planCategoryAmounts")
            for (x in it) {
                planCategoryAmounts[x.category] = BehaviorSubject.createDefault(x.amount)
            }
        }
        // ## Bind planCategoryAmounts -> Repo
        planCategoryAmounts.observable.observeOn(Schedulers.io()).subscribe {
            logz("planCategoryAmounts -> Repo")
            repo.clearPlanCategoryAmounts()
            for ((categoryName, amountBehaviorSubject) in it) {
                repo.addPlanCategoryAmounts(PlanCategoryAmounts(
                    categoryName,
                    amountBehaviorSubject.value
                ))
            }
        }
    }
}