package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.nav
import com.tminus1010.budgetvalue.extensions.v
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipe
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.reflectXY
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_category_customization.view.*

class CategoryCustomizationFrag : Fragment(R.layout.frag_category_customization) {
    val app by lazy { requireActivity().application as App }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
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
                v.clicks().subscribeOn(AndroidSchedulers.mainThread()).map { d }.subscribe(categoriesAppVM.intentDeleteCategory)
            }
        )
        val recipeGrid = RecipeGrid(listOf(
            factory1.createMany(categoriesAppVM.categories.value).toList(),
            factory2.createMany(categoriesAppVM.categories.value).toList(),
        ).reflectXY())
        v.tmTableView.initialize(recipeGrid = recipeGrid)
    }
}