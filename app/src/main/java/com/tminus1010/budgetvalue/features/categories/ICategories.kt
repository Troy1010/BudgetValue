package com.tminus1010.budgetvalue.features.categories

import io.reactivex.rxjava3.subjects.BehaviorSubject

interface ICategories {
    val defaultCategory: Category
    val unknownCategory: Category
    val userCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}