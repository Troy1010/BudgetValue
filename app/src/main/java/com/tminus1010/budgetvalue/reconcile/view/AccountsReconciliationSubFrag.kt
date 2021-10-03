package com.tminus1010.budgetvalue.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemEmptyRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemMoneyEditTextRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTitledDividerRB
import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountVMItem
import com.tminus1010.budgetvalue.all.presentation_and_view._models.ValidatedStringVMItem
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.budgetvalue.reconcile.presentation.AccountsReconciliationVM
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables

@AndroidEntryPoint
class AccountsReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    lateinit var vb: ItemTmTableViewBinding
    val accountsReconciliationVM by viewModels<AccountsReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        // # Presentation State
        vb.tmTableView.bind(Observables.combineLatest(accountsReconciliationVM.recipeGrid, accountsReconciliationVM.dividerMap))
        { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { recipeList ->
                    recipeList.map {
                        when (it) {
                            null -> itemEmptyRF().create(hasHighlight = true)
                            is String -> itemTextViewRB().create(it)
                            is ValidatedStringVMItem -> itemTextViewRB().create(it)
                            is CategoryAmountVMItem -> itemMoneyEditTextRF().create(it)
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

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.accountsReconciliationSubFrag)
        }
    }
}