package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragAdvancedCategorizeBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.middleware.reflectXY
import com.tminus1010.budgetvalue.middleware.ui.ViewItemRecipeFactoryProvider
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CategorizeAdvancedFrag : Fragment(R.layout.frag_advanced_categorize), IViewModels {
    val vb by viewBinding(FragAdvancedCategorizeBinding::bind)
    val viewRecipeFactories by lazy { ViewItemRecipeFactoryProvider(requireContext()) }
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnDone.setOnClickListener {
            if (categorizeAdvancedVM.defaultAmount.value.compareTo(BigDecimal.ZERO)!=0) {
                toast("Default must be 0")
            } else {
                categorizeAdvancedVM.intentPushActiveCategories.onNext(Unit)
                nav.navigateUp()
            }
        }
        // # TMTableView
        val cellRecipeFactory = viewRecipeFactories.cellRecipeFactory
        val headerRecipeFactory = viewRecipeFactories.headerRecipeFactory
        val amountRecipeFactory = viewRecipeFactories.incomingBigDecimalRecipeFactory
        val categoryAmountRecipeFactory = viewRecipeFactories.outgoingCARecipeFactory(categorizeAdvancedVM.intentRememberCA)
        val titledDividerRecipeFactory = viewRecipeFactories.titledDividerRecipeFactory
        categoriesVM.userCategories
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { categories ->
                val recipes2D = listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(categories.map { it.name }),
                    headerRecipeFactory.createOne2("Amount")
                            + amountRecipeFactory.createOne2(categorizeAdvancedVM.defaultAmount)
                            + categoryAmountRecipeFactory.createMany(categories.map { it to BigDecimal.ZERO })
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.tmTableView.initialize(recipes2D, true, dividerMap, 0, 1)
            }
    }
}