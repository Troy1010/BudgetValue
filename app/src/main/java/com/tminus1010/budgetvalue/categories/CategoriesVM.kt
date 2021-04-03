package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain2
import com.tminus1010.budgetvalue.categories.domain.ICategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.ICategoriesDomain2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesVM @Inject constructor(
    categoriesDomain: CategoriesDomain,
    categoriesDomain2: CategoriesDomain2
) : ViewModel(),
    ICategoriesDomain by categoriesDomain,
    ICategoriesDomain2 by categoriesDomain2