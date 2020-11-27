package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.getTotalObservable
import com.example.budgetvalue.layer_data.Repo
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject

class PlanVM(repo: Repo) : ViewModel() {
    val planCategoryAmounts = repo.planCategoryAmounts
    val uncategorizedPlan = planCategoryAmounts
        .flatMap { it.itemObservablesObservable }
        .flatMap { getTotalObservable(it.values) }
        .toBehaviorSubject()
    val expectedIncome = BehaviorSubject.createDefault(repo.fetchExpectedIncome())
        // # Bind expectedIncome -> Repo
        .also { it.skip(1).subscribe { repo.pushExpectedIncome(it) } }
    val difference = combineLatestAsTuple(expectedIncome, uncategorizedPlan)
        .map { it.first - it.second }
}