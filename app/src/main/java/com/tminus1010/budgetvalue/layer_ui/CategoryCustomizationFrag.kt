package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions_intersecting.repo
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.tmcommonkotlin.rx.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.v
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.frag_category_customization.view.*

class CategoryCustomizationFrag : Fragment(R.layout.frag_category_customization) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        v.btn_done.setOnClickListener { nav.navigateUp() }
        // # TMTableView
        val factory1 = ViewItemRecipeFactory(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v: TextView, d: Category -> v.text = d.name }
        )
        val factory2 = ViewItemRecipeFactory(
            { View.inflate(requireContext(), R.layout.button, null) as Button },
            { v: Button, d: Category ->
                v.text = "Delete"
                v.setOnClickListener { repo.deleteFromActive(d) }
                v.isEnabled = !d.isRequired
            }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        repo.activeCategories
            .observeOn(Schedulers.computation())
            .map { categories ->
                val recipeGrid = RecipeGrid(listOf(
                    factory1.createMany(categories),
                    factory2.createMany(categories),
                ).reflectXY())
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key }
                Pair(recipeGrid, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { v.tmTableView.initialize(recipeGrid = it.first, dividerMap = it.second) }
    }
}