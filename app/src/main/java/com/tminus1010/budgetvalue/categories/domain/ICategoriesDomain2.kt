package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue.categories.Category
import io.reactivex.rxjava3.subjects.PublishSubject

interface ICategoriesDomain2 {
    val intentDeleteCategoryFromActive: PublishSubject<Category>
}