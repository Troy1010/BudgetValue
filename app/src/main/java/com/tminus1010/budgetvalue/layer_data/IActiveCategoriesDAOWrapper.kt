package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IActiveCategoriesDAOWrapper: ActiveCategoriesDAO {
    val defaultCategory: Category
    val activeCategories: BehaviorSubject<List<Category>>
    val categories: BehaviorSubject<List<Category>>
}