package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.CellRecipeFactory
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
        val cellRecipeBuilder = CellRecipeFactory(requireContext(), CellRecipeFactory.DefaultType.CELL)
        val headerRecipeBuilder = CellRecipeFactory(requireContext(), CellRecipeFactory.DefaultType.HEADER)
        val inputRecipeBuilder = CellRecipeFactory<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, bs -> v.rxBind(bs, { it.toBigDecimal2() } )}
        )
        val oneWayCellRecipeBuilder = CellRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.rxBindOneWay(bs)}
        )
        planVM.planCategoryAmounts.itemObservablesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .observe(this) {
                myTableView_plan.setRecipes(
                    listOf(
                        headerRecipeBuilder.createOne("Category")
                                + cellRecipeBuilder.createOne("Expected Income")
                                + cellRecipeBuilder.createOne("Default")
                                + cellRecipeBuilder.createMany(it.keys.map { it.name }),
                        headerRecipeBuilder.createOne("Plan")
                                + inputRecipeBuilder.createOne(planVM.expectedIncome)
                                + oneWayCellRecipeBuilder.createOne(planVM.difference)
                                + inputRecipeBuilder.createMany(it.values)
                    ).reflectXY()
                )
            }
    }
}