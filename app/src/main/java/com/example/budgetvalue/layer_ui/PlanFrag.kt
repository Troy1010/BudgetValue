package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.CellRecipeBuilder
import com.example.budgetvalue.layer_ui.misc.rxBind
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import com.example.budgetvalue.reflectXY
import com.example.budgetvalue.toBigDecimal2
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_plan.*
import java.math.BigDecimal

class PlanFrag: Fragment(R.layout.frag_plan) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM : PlanVM by activityViewModels { createVmFactory { PlanVM(repo, categoriesAppVM) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        val cellRecipeBuilder = CellRecipeBuilder(requireContext(), CellRecipeBuilder.DefaultType.CELL)
        val headerRecipeBuilder = CellRecipeBuilder(requireContext(), CellRecipeBuilder.DefaultType.HEADER)
        val inputRecipeBuilder = CellRecipeBuilder<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, bs -> v.rxBind(bs, { it.toBigDecimal2() } )}
        )
        val oneWayCellRecipeBuilder = CellRecipeBuilder<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.rxBindOneWay(bs)}
        )
        planVM.planCategoryAmounts.itemObservablesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .observe(this) {
                myTableView_plan.setRecipes(
                    listOf(
                        headerRecipeBuilder.buildOne("Category")
                                + cellRecipeBuilder.buildOne("Expected Income")
                                + cellRecipeBuilder.buildOne("Default")
                                + cellRecipeBuilder.buildMany(it.keys.map { it.name }),
                        headerRecipeBuilder.buildOne("Plan")
                                + inputRecipeBuilder.buildOne(planVM.expectedIncome)
                                + oneWayCellRecipeBuilder.buildOne(planVM.difference)
                                + inputRecipeBuilder.buildMany(it.values)
                    ).reflectXY()
                )
            }
    }
}