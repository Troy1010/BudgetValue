package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.*
import com.example.budgetvalue.layer_ui.TMTableView.CellRecipeBuilder.DefaultType
import com.example.budgetvalue.layer_ui.misc.rxBind
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import com.example.budgetvalue.reflectXY
import com.example.budgetvalue.toBigDecimal2
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_split.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class SplitFrag : Fragment(R.layout.frag_split) {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val categoriesVM: CategoriesVM by activityViewModels { createVmFactory { CategoriesVM() } }
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(appComponent.getRepo()) } }
    val accountsVM: AccountsVM by activityViewModels{ createVmFactory { AccountsVM(appComponent.getRepo()) }}
    val splitVM: SplitVM by activityViewModels { createVmFactory { SplitVM(appComponent.getRepo(), categoriesVM, transactionsVM.spends, accountsVM.accounts ) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTableDataObserver()
    }

    fun setupTableDataObserver() {
        val cellRecipeBuilder = CellRecipeBuilder(requireContext(), DefaultType.CELL)
        val headerRecipeBuilder = CellRecipeBuilder(requireContext(), DefaultType.HEADER)
        val headerRecipeBuilder_numbered = CellRecipeBuilder<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            {v, d ->
                v.textview_header.text = d.first
                v.textview_number.rxBindOneWay(d.second)
            })
        val incomeRecipeBuilder = CellRecipeBuilder<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, bs -> v.rxBind(bs, { it.toBigDecimal2() } )}
        )
        val oneWayCellRecipeBuilder = CellRecipeBuilder<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_basic_cell, null) as TextView },
            { v, observable -> v.rxBindOneWay(observable)}
        )
        splitVM.rowDatas
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) {
                val rowDatas = it
                val categories = rowDatas
                    .map { it.category.name }
                val spents = rowDatas
                    .map { it.spent.toString() }
                val incomes = rowDatas
                    .map { it.income }
                val budgeteds = rowDatas
                    .map { rowData -> rowData.income.map { rowData.getBudgeted2(it) } }
                myTableView_1.setRecipes(
                    listOf(
                        headerRecipeBuilder.buildOne("Category")
                                + cellRecipeBuilder.buildOne("Default")
                                + cellRecipeBuilder.buildMany(categories),
                        headerRecipeBuilder.buildOne("Spent")
                                + oneWayCellRecipeBuilder.buildOne(splitVM.spentLeftToCategorize)
                                + cellRecipeBuilder.buildMany(spents),
                        headerRecipeBuilder.buildOne("Income")
                                + oneWayCellRecipeBuilder.buildOne(splitVM.incomeLeftToCategorize)
                                + incomeRecipeBuilder.buildMany(incomes),
                        headerRecipeBuilder_numbered.buildOne(Pair("Budgeted",splitVM.incomeTotal))
                                + oneWayCellRecipeBuilder.buildOne(splitVM.uncategorizedBudgeted)
                                + oneWayCellRecipeBuilder.buildMany(budgeteds)
                    ).reflectXY()
                )
            }
    }
}
