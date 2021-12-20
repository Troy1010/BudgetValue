package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.transactions.app.SubFragEventProvider
import com.tminus1010.budgetvalue.transactions.view.ChooseAmountSubFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryVM @Inject constructor(
    categoriesInteractor: CategoriesInteractor,
    subFragEventProvider: SubFragEventProvider,
) : ViewModel() {
    // # Sub VMItems
    val categoryButtonVMItems =
        categoriesInteractor.userCategories.asFlow()
            .map {
                it.map {
                    ButtonVMItem(
                        title = it.name,
                        onClick = {
                            logz("Category selected:$it")
                            subFragEventProvider.showFragment.easyEmit(ChooseAmountSubFrag())
                        },
                    )
                }
            }
}
