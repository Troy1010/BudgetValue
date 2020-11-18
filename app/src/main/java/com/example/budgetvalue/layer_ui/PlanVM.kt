package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.SourceArrayList
import com.example.budgetvalue.SourceHashMap
import com.example.budgetvalue.layer_data.Repo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class PlanVM(categoriesVM: CategoriesVM): ViewModel() {
    val planCategoryAmounts = SourceHashMap<String, BehaviorSubject<BigDecimal>>()
    init {
        categoriesVM.categories.subscribe { categories ->
            for ((categoryName, amountObservable) in planCategoryAmounts) {
                if (categoryName !in categories.map { it.name })
                    planCategoryAmounts.remove(categoryName)
            }
            for (categoryName in categories.map { it.name }) {
                if (categoryName !in planCategoryAmounts)
                    planCategoryAmounts[categoryName] = BehaviorSubject.createDefault(BigDecimal.ZERO)
            }
        }
    }
}