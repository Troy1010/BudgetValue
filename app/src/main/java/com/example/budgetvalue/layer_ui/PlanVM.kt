package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.combineLatestWithIndex
import com.example.budgetvalue.extensions.total
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentPushPlanCategoryAmount = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.flatMapCompletable(repo::pushPlanCategoryAmount).subscribe() }
    val statePlanCAs = combineLatestWithIndex(intentPushPlanCategoryAmount, repo.planCategoryAmounts)
        .scan(SourceHashMap<Category, BigDecimal>()) { acc, (x0, x1, i) ->
            when (i) {
                0 -> acc[x0.first] = x0.second
                1 -> { acc.clear(); acc.putAll(x1) }
            }
            acc
        }
        .toBehaviorSubject()
    val statePlanUncategorized = statePlanCAs
        .switchMap { it.observable }
        .flatMap { it.values.total() }
        .replay(1).refCount()
    val stateExpectedIncome = intentPushExpectedIncome
        .startWithItem(repo.fetchExpectedIncome())
    val stateDifference = combineLatestAsTuple(stateExpectedIncome, statePlanUncategorized)
        .map { it.first - it.second }
}