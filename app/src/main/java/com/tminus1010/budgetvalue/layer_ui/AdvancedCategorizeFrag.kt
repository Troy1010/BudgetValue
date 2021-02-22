package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.distinctUntilChangedWith
import com.tminus1010.budgetvalue.extensions.nav
import com.tminus1010.budgetvalue.extensions_intersecting.advancedCategorizeVM
import com.tminus1010.budgetvalue.extensions_intersecting.repo
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.tmcommonkotlin.misc.toast
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.frag_advanced_categorize.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class AdvancedCategorizeFrag : Fragment(R.layout.frag_advanced_categorize) {
    val viewRecipeFactories by lazy { ViewItemRecipeFactoryProvider(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        btn_ac_done.setOnClickListener {
            if (advancedCategorizeVM.defaultAmount.value.compareTo(BigDecimal.ZERO)!=0) {
                toast("Default must be 0")
            } else {
                repo.updateTransactionCategoryAmounts(
                    advancedCategorizeVM.transactionToPush.value.id,
                    advancedCategorizeVM.transactionToPush.value.categoryAmounts.mapKeys { it.key.name }
                ).subscribeOn(Schedulers.io()).subscribe()
                nav.navigateUp()
            }
        }
        // # TMTableView
        val cellRecipeFactory = viewRecipeFactories.cellRecipeFactory
        val headerRecipeFactory = viewRecipeFactories.headerRecipeFactory
        val amountRecipeFactory = viewRecipeFactories.incomingBigDecimalRecipeFactory
        val categoryAmountRecipeFactory = viewRecipeFactories.outgoingCARecipeFactory(advancedCategorizeVM.intentRememberCA)
        val titledDividerRecipeFactory = viewRecipeFactories.titledDividerRecipeFactory
        combineLatestAsTuple(tmTableView_ac.widthObservable, repo.activeCategories)
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (width, categories) ->
                val recipes2D = RecipeGrid(listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(categories.map { it.name }),
                    headerRecipeFactory.createOne2("Amount")
                            + amountRecipeFactory.createOne2(advancedCategorizeVM.defaultAmount)
                            + categoryAmountRecipeFactory.createMany(categories.map { it to BigDecimal.ZERO }))
                    .reflectXY(), fixedWidth = width)
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                tmTableView_ac.initialize(recipes2D, dividerMap, 0, 1)
            }
    }
}