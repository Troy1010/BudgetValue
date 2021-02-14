package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.total
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.mergeCombineWithIndex
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class ActivePlanVM(val repo: Repo, categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentPushExpectedIncome = PublishSubject.create<BigDecimal>()
        .also { it.subscribe(repo::pushExpectedIncome) }
    val intentPushPlanCA = PublishSubject.create<Pair<Category, BigDecimal>>()
        .also { it.subscribe { repo.pushActivePlanCA(it) } }

    val activePlan = mergeCombineWithIndex(
        repo.activePlan,
        intentPushPlanCA,
        categoriesAppVM.choosableCategories,
    )
        .scan(SourceHashMap<Category, BigDecimal>(exitValue = BigDecimal(0))) { acc, (i, activePlan, intentPushPlanCA, chooseableCategories) ->
            when (i) {
                0 -> { activePlan!!
                    acc.clear()
                    acc.putAll(activePlan)
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
    val planUncategorized = activePlan.value.itemObservableMap2
        .switchMap { it.values.total() }
        .replay(1).refCount()
    val expectedIncome = intentPushExpectedIncome
        .startWithItem(repo.fetchExpectedIncome())
    val defaultAmount = combineLatestAsTuple(expectedIncome, planUncategorized)
        .map { it.first - it.second }
    val activeCategories = activePlan
        .map { it.keys }
        .map { it.sortedWith(categoryComparator) }
}