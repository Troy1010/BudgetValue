package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.model_domain.Category
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IActiveCategoriesDAOWrapper {
    val defaultCategory: Category
    val activeCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
    val unknownCategory: Category
    fun push(category: Category): Completable
    fun delete(category: Category): Completable
}