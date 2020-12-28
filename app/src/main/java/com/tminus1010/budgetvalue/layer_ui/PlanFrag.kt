package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_app.Category
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
    val planVM : PlanVM by activityViewModels2 { PlanVM(repo, categoriesAppVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinds()
    }

    private fun setupBinds() {
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val inputRecipeFactory = ViewItemRecipeFactory<EditText, Pair<BigDecimal, Subject<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, (state, actionSubject) -> v.setText("$state"); v.bindOutgoing(actionSubject, { it.toBigDecimalSafe() } ) }
        )
        val planCAsRecipeFactory2 = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, (k, v) ->
                view.bindIncoming(v)
                view.bindOutgoing(planVM.intentPushPlanCA, { s -> Pair(k, s.toBigDecimalSafe()) } )
            }
        )
        val oneWayCellRecipeBuilder = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.bindIncoming(bs) }
        )
        combineLatestAsTuple(planVM.planCAs, planVM.expectedIncome)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            //*Without distinctUntilChanged, state changes are needlessly pushed to
            // ui when user uses an edit text.
            .distinctUntilChanged()
            .map { (planCAs, expectedIncome) ->
                listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Expected Income")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(planCAs.keys.map { it.name }),
                    headerRecipeFactory.createOne2("Plan")
                            + inputRecipeFactory.createOne2(Pair(expectedIncome,
                        planVM.intentPushExpectedIncome))
                            + oneWayCellRecipeBuilder.createOne2(planVM.difference)
                            + planCAsRecipeFactory2.createMany(planCAs.observableMap.map { Pair(it.key, it.value) })
                ).reflectXY()
            }
            .observe(this) { myTableView_plan.setRecipes(it) }
    }
}