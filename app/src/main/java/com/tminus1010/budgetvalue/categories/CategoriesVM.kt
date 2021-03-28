package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class CategoriesVM @Inject constructor(
    categoriesDomain: CategoriesDomain
) : ViewModel(), ICategoriesDomain by categoriesDomain