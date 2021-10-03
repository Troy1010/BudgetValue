package com.tminus1010.budgetvalue.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemEmptyRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemMoneyEditTextRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.budgetvalue.reconcile.presentation.AccountsReconciliationVM
import com.tminus1010.budgetvalue.reconcile.presentation.model.CategoryAmountVMItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    lateinit var vb: ItemTmTableViewBinding
    val accountsReconciliationVM by viewModels<AccountsReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        vb.tmTableView.bind(accountsReconciliationVM.recipeGrid) { recipeGrid ->
            initialize(
                recipeGrid = recipeGrid.map { recipeList ->
                    recipeList.map {
                        when (it) {
                            null -> itemEmptyRF().create(hasHighlight = true)
                            is String -> itemTextViewRB().create(it)
                            is CategoryAmountVMItem -> itemMoneyEditTextRF().create(it)
                            else -> error("Unhandled:$it")
                        }
                    }
                },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.accountsReconciliationSubFrag)
        }
    }
}