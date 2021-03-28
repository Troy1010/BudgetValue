package com.tminus1010.budgetvalue.categories

import io.reactivex.rxjava3.observables.ConnectableObservable
import io.reactivex.rxjava3.subjects.BehaviorSubject


interface ICategoriesDomain : ICategoryParser {
    val defaultCategory: Category
    val unknownCategory: Category
    val userCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
    val nameToCategoryMap: ConnectableObservable<HashMap<String, Category>>
}