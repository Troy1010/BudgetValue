package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragCategoryCustomizationBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.misc.viewBinding
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features_shared.IViewModels
import com.tminus1010.budgetvalue.middleware.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CategoryCustomizationFrag : Fragment(R.layout.frag_category_customization), IViewModels {
    val binding by viewBinding(FragCategoryCustomizationBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        binding.btnDone.setOnClickListener { nav.navigateUp() }
        // # TMTableView
        val factory1 = ViewItemRecipeFactory(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v: TextView, d: Category -> v.text = d.name }
        )
        val factory2 = ViewItemRecipeFactory(
            { View.inflate(requireContext(), R.layout.button, null) as Button },
            { v: Button, d: Category ->
                v.text = "Delete"
                v.setOnClickListener { domain.deleteFromActive(d).launch() }
                v.isEnabled = !d.isRequired
            }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        domain.userCategories
            .observeOn(Schedulers.computation())
            .map { categories ->
                val recipeGrid = listOf(
                    factory1.createMany(categories),
                    factory2.createMany(categories),
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key }
                Pair(recipeGrid, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { binding.tmTableView.initialize(recipeGrid = it.first, dividerMap = it.second) }
    }
}