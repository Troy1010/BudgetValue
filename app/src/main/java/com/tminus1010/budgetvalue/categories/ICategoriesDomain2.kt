package com.tminus1010.budgetvalue.categories

import io.reactivex.rxjava3.subjects.PublishSubject

interface ICategoriesDomain2 {
    val intentDeleteCategoryFromActive: PublishSubject<Category>
}