package com.tminus1010.budgetvalue.modules.categories

import com.tminus1010.budgetvalue.modules.categories.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface ICategories {
    val defaultCategory: Category
    val unknownCategory: Category
    val userCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}