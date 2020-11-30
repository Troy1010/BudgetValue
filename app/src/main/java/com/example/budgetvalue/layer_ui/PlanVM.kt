package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.total
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.mergeWithIndex
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentPushPlanCategoryAmount = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.flatMapCompletable(repo::pushPlanCategoryAmount).subscribe() }
    // TODO("feed response into state, not intent")
    val statePlanCAs = mergeWithIndex(intentPushPlanCategoryAmount, repo.planCategoryAmounts, categoriesAppVM.choosableCategories)
        .scan(SourceHashMap<Category, BigDecimal>()) { acc, (i, intentPushPlanCA, responsePlanCAs, stateChooseableCategories) ->
            when (i) {
                0 -> { intentPushPlanCA!!; acc[intentPushPlanCA.first] = intentPushPlanCA.second }
                1 -> { responsePlanCAs!!
                    acc.clear() // TODO("clear might ruin pairwise totals, maybe use onComplete?")
                    acc.putAll(responsePlanCAs)
                    if (stateChooseableCategories!=null)
                        acc.putAll(stateChooseableCategories
                            .associate { it to BigDecimal.ZERO }
                            .filter { kv -> kv.key !in acc.keys })
                }
                2 -> { stateChooseableCategories!!
                    acc.putAll(stateChooseableCategories
                        .associate { it to BigDecimal.ZERO }
                        .filter { kv -> kv.key !in acc.keys })
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