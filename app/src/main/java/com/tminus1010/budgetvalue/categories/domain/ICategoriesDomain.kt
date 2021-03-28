package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.categories.ICategoryParser
import io.reactivex.rxjava3.observables.ConnectableObservable
import io.reactivex.rxjava3.subjects.BehaviorSubject


interface ICategoriesDomain : ICategoryParser {
    val defaultCategory: Category
    val unknownCategory: Category
    val userCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
    val nameToCategoryMap: ConnectableObservable<HashMap<String, Category>>
}