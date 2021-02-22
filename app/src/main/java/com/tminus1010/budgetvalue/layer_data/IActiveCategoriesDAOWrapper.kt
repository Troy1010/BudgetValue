package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IActiveCategoriesDAOWrapper {
    val defaultCategory: Category
    val activeCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}