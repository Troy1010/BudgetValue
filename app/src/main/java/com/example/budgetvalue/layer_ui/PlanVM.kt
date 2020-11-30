package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.extensions.total
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val actionPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
//    val actionsPushPlanCategoryAmount = SourceHashMap<Category, BigDecimal>()
//        .also {
//            // # Feed choosableCategories into it
//            // TODO("simplify")
//            categoriesAppVM.choosableCategories.subscribe { categories ->
//                categories
//                    .filter { category -> category !in it.observableMap.keys }
//                    .forEach { category -> it.prepareKey(category) }
//            }
//        }
//        .also { repo.bindToPlanCategoryAmounts(it) }

    val actionPushPlanCategoryAmount = PublishSubject.create<Pair<Category, BigDecimal>>()
    val statePlanCAs = Observable.merge(actionPushPlanCategoryAmount, repo.planCategoryAmounts)
        .scan(SourceHashMap<Category, BigDecimal>()) { acc, x:Any ->
            // TODO("simplify")
            if (x is Pair<*, *>) {
                x as Pair<Category, BigDecimal>
                acc[x.first] = x.second
            } else if (x is Map<*, *>) {
                x as Map<Category, BigDecimal>
                acc.clear(); acc.putAll(x)
            }
            acc
        }
        .toBehaviorSubject()
    val statePlanUncategorized = statePlanCAs
        .switchMap { it.observable }
        .flatMap { it.values.total() }
        .replay(1).refCount()
    val stateExpectedIncome = actionPushExpectedIncome
        .startWithItem(repo.fetchExpectedIncome())
    val stateDifference = combineLatestAsTuple(stateExpectedIncome, statePlanUncategorized)
        .map { it.first - it.second }
}