package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.SourceHashMap
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.total
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.mergeCombineWithIndex
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class PlanVM(repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.flatMapCompletable(repo::pushPlanCategoryAmount).subscribe() }
    val planCAs = mergeCombineWithIndex(
        repo.planCategoryAmounts,
        intentPushPlanCA,
        categoriesAppVM.choosableCategories,
    )
        .scan(SourceHashMap<Category, BigDecimal>()) { acc, (i, responsePlanCAs, intentPushPlanCA, chooseableCategories) ->
            when (i) {
                0 -> { responsePlanCAs!!
                    acc.clear()
                    acc.putAll(responsePlanCAs)
                    if (chooseableCategories!=null)
                        acc.putAll(chooseableCategories
                            .filter { it !in acc.keys }
                            .associate { it to BigDecimal.ZERO })
                }
                1 -> { intentPushPlanCA!!.also { (k, v) -> acc[k] = v } }
                2 -> { chooseableCategories!!
                    acc.putAll(chooseableCategories
                        .filter { it !in acc.keys }
                        .associate { it to BigDecimal.ZERO })
                }
            }
            acc
        }
        .toBehaviorSubject()
    val planUncategorized = planCAs
        .switchMap { it.observable }
        .flatMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWithItem(repo.fetchExpectedIncome())
    val difference = combineLatestAsTuple(expectedIncome, planUncategorized)
        .map { it.first - it.second }
}