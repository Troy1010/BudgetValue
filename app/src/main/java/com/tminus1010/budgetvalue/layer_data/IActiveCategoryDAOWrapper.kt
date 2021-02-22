package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IActiveCategoryDAOWrapper {
    val defaultCategory: Category
    val activeCategories: Observable<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}