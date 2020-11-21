package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import kotlinx.android.synthetic.main.frag_reconcile.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class PlanFrag: Fragment(R.layout.frag_plan) {
    val categoriesVM : CategoriesVM by viewModels { createVmFactory { CategoriesVM() } }
    val planVM : PlanVM by viewModels { createVmFactory { PlanVM(categoriesVM) } }

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
        planVM.planCategoryAmounts.observable
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