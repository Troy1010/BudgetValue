package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue._core.middleware.Rx
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

// Separate from CategoriesVM to avoid circular dependency graph
@HiltViewModel
class CategoriesVM2 @Inject constructor(
    categoriesDomain2: CategoriesDomain2
) : ViewModel(), ICategoriesDomain2 by categoriesDomain2