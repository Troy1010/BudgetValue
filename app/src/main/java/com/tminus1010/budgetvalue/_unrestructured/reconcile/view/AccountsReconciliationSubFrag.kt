package com.tminus1010.budgetvalue._unrestructured.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.framework.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue.framework.view.recipe_factories.itemTitledDividerRB
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.budgetvalue._unrestructured.reconcile.presentation.AccountsReconciliationVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class AccountsReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    private lateinit var vb: ItemTmTableViewBinding
    private val viewModel by viewModels<AccountsReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        // # State
        vb.tmTableView.bind(combine(viewModel.recipeGrid, viewModel.dividerMap) { a, b -> Pair(a, b) })
        { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { recipeList ->
                    recipeList.map {
                        when (it) {
                            is String -> itemTextViewRB().create(it)
                            is IHasToViewItemRecipe -> it.toViewItemRecipe(context)
                            else -> error("Unhandled:$it")
                        }
                    }
                },
                dividerMap = dividerMap.mapValues { itemTitledDividerRB().create(it.value) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1
            )
        }
    }
}