package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.CellRecipeBuilder
import com.example.budgetvalue.layer_ui.misc.rxBind
import com.example.budgetvalue.reflectXY
import com.example.budgetvalue.toBigDecimal2
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_plan.*
import java.math.BigDecimal

class PlanFrag: Fragment(R.layout.frag_plan) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val categoriesVM : CategoriesVM by activityViewModels { createVmFactory { CategoriesVM() } }
    val planVM : PlanVM by activityViewModels { createVmFactory { PlanVM(repo, categoriesVM) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        val cellRecipeBuilder = CellRecipeBuilder(requireContext(), CellRecipeBuilder.DefaultType.CELL)
        val headerRecipeBuilder = CellRecipeBuilder(requireContext(), CellRecipeBuilder.DefaultType.HEADER)
        val incomeRecipeBuilder = CellRecipeBuilder<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, bs -> v.rxBind(bs, { it.toBigDecimal2() } )}
        )
        planVM.planCategoryAmounts.itemObservablesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .observe(this) {
                myTableView_plan.setRecipes(
                    listOf(
                        headerRecipeBuilder.buildOne("Category")
                                + cellRecipeBuilder.buildMany(it.keys.toList()),
                        headerRecipeBuilder.buildOne("Plan")
                                + incomeRecipeBuilder.buildMany(it.values.toList())
                    ).reflectXY()
                )
            }
    }
}