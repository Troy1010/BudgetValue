package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.total
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.mergeWithType
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentPushPlanCategoryAmount = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.flatMapCompletable(repo::pushPlanCategoryAmount).subscribe() }
    val statePlanCAs = mergeWithType(intentPushPlanCategoryAmount, repo.planCategoryAmounts, categoriesAppVM.choosableCategories)
        .scan(SourceHashMap<Category, BigDecimal>()) { acc, (i, intentPushPlanCA, responsePlanCAs, stateChooseableCategories) ->
            when (i) {
                0 -> { intentPushPlanCA!!; acc[intentPushPlanCA.first] = intentPushPlanCA.second }
                1 -> { responsePlanCAs!!
                    acc.clear() // TODO("clear might ruin pairwise totals, maybe use onComplete?")
                    acc.putAll(responsePlanCAs)
                    stateChooseableCategories
                        ?.filter { category -> category !in acc.observableMap.keys }
                        ?.forEach { category -> acc[category] = BigDecimal.ZERO }
                }
                2 -> { stateChooseableCategories!!
                    stateChooseableCategories
                        .filter { category -> category !in acc.observableMap.keys }
                        .forEach { category -> acc[category] = BigDecimal.ZERO }
                }
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