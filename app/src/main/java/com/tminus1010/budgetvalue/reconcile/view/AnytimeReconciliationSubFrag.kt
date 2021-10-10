package com.tminus1010.budgetvalue.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemEmptyRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemMoneyEditTextRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTitledDividerRB
import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.AmountPresentationModel
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.budgetvalue.reconcile.presentation.AnytimeReconciliationVM
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables

@AndroidEntryPoint
class AnytimeReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    lateinit var vb: ItemTmTableViewBinding
    val anytimeReconciliationVM by viewModels<AnytimeReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        vb.tmTableView.bind(Observables.combineLatest(anytimeReconciliationVM.recipeGrid, anytimeReconciliationVM.dividerMap))
        { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { recipeList ->
                    recipeList.map {
                        when (it) {
                            null -> itemEmptyRF().create(hasHighlight = true)
                            is String -> itemTextViewRB().create(it)
                            is AmountPresentationModel -> itemTextViewRB().create(it)
                            is CategoryAmountPresentationModel -> itemMoneyEditTextRF().create(it)
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