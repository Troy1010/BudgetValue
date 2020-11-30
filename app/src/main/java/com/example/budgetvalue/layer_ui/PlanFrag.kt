package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.*
import com.example.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.example.budgetvalue.layer_ui.misc.bindIncoming
import com.example.budgetvalue.layer_ui.misc.bindOutgoing
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
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
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val inputRecipeFactory = ViewItemRecipeFactory<EditText, Pair<BigDecimal, Subject<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, (state, actionSubject) -> v.setText("$state"); v.bindOutgoing(actionSubject, { it.toBigDecimalSafe() } ) }
        )
        val inputRecipeFactory2 = ViewItemRecipeFactory<EditText, Triple<BigDecimal, Subject<Pair<Category, BigDecimal>>, Category>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, (state, actionSubject, category) -> v.setText("$state"); v.bindOutgoing(actionSubject, { Pair(category, it.toBigDecimalSafe()) } ) }
        )
        val oneWayCellRecipeBuilder = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.bindIncoming(bs) }
        )
        combineLatestAsTuple(planVM.statePlanCAs, planVM.stateExpectedIncome)
            .observeOn(AndroidSchedulers.mainThread())
            //*Without this, state changes will be needlessly pushed to
            // ui when user uses an edit text.
            .distinctUntilChanged()
            .map {
                listOf(
                    headerRecipeFactory.createOne("Category")
                            + cellRecipeFactory.createOne("Expected Income")
                            + cellRecipeFactory.createOne("Default")
                            + cellRecipeFactory.createMany(it.first.keys.map { it.name }),
                    headerRecipeFactory.createOne("Plan")
                            + inputRecipeFactory.createOne(Pair(it.second,
                        planVM.actionPushExpectedIncome))
                            + oneWayCellRecipeBuilder.createOne(planVM.stateDifference)
                            + inputRecipeFactory2.createMany(it.first.map { kv -> Triple(
                        it.first[kv.key]?:BigDecimal.ZERO,
                        planVM.actionPushPlanCategoryAmount,
                        kv.key
                    )})
                )
            }
            .observe(this) { myTableView_plan.setRecipes(it.reflectXY()) }
    }
}