package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface ICategories {
    val defaultCategory: Category
    val unknownCategory: Category
    val activeCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}