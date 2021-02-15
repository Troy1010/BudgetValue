package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.extensions.distinctUntilChangedWith
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.frag_advanced_categorize.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class AdvancedCategorizeFrag : Fragment(R.layout.frag_advanced_categorize) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val viewRecipeFactories by lazy { ViewRecipeFactories(requireContext()) }
    val advancedCategorizeVM by activityViewModels2 { AdvancedCategorizeVM() }
    val nav by lazy { findNavController() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Btn Done
        btn_ac_done.setOnClickListener {
            nav.navigateUp()
        }
        // # TMTableView
        val cellRecipeFactory = viewRecipeFactories.cellRecipeFactory
        val headerRecipeFactory = viewRecipeFactories.headerRecipeFactory
        val amountRecipeFactory = viewRecipeFactories.outgoingBigDecimalRecipeFactory(advancedCategorizeVM.intentRememberAmount)
        val categoryAmountRecipeFactory = viewRecipeFactories.outgoingCARecipeFactory(advancedCategorizeVM.intentRememberCA)
        val titledDividerRecipeFactory = viewRecipeFactories.titledDividerRecipeFactory
        combineLatestAsTuple(tmTableView_ac.widthObservable, categoriesAppVM.choosableCategories)
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (width, categories) ->
                val recipes2D = RecipeGrid(listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(categories.map { it.name }),
                    headerRecipeFactory.createOne2("Amount")
                            + amountRecipeFactory.createOne2(BigDecimal.ZERO)
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
            .observe(this) { (recipes2D, dividerMap) ->
                tmTableView_ac.initialize(recipes2D, dividerMap, 0, 1)
            }
    }
}