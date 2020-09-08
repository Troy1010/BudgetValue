package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.layers.view_models.AccountsVM
import com.example.budgetvalue.layers.view_models.SplitVM
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.budgetvalue.layers.z_ui.TMTableView.*
import com.example.budgetvalue.layers.z_ui.TMTableView.CellRecipeBuilder.Default
import com.example.budgetvalue.util.combineLatestAsTuple
import com.example.budgetvalue.util.reflectXY
import com.example.tmcommonkotlin.vmFactoryFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_split.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class SplitFrag : Fragment(R.layout.frag_split) {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val categoriesVM: CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val accountsVM: AccountsVM by activityViewModels{ vmFactoryFactory { AccountsVM(appComponent.getRepo()) }}
    val splitVM: SplitVM by activityViewModels { vmFactoryFactory { SplitVM(appComponent.getRepo(), categoriesVM, transactionsVM.transactions, accountsVM.accounts ) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTableDataObserver()
    }

    fun setupTableDataObserver() {
        val cellRecipeBuilder = CellRecipeBuilder(requireContext(), Default.CELL)
        val headerRecipeBuilder = CellRecipeBuilder(requireContext(), Default.HEADER)
        val incomeHeaderRecipeBuilder = CellRecipeBuilder<LinearLayout, Pair<String, BigDecimal>>({ View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            {v, d ->
                v.textview_header.text = d.first
                v.textview_number.text = d.second.toString()
            })
        val incomeRecipeBuilder = CellRecipeBuilder(requireContext(), Default.HEADER)
        combineLatestAsTuple(
            splitVM.rowDatas,
            splitVM.incomeTotal
        ).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(viewLifecycleOwner).subscribe {
            val rowDatas = it.first
            val categories = rowDatas.map { it.category.name }
            val spents = rowDatas.map { it.spent.toString() }
            val incomes = rowDatas.map { it.income.toString() }
            val budgeteds = rowDatas.map { it.budgeted.toString() }
            myTableView_1.setRecipes(
                listOf(
                    listOf(headerRecipeBuilder.buildOne("Category")) + cellRecipeBuilder.build(categories),
                    listOf(headerRecipeBuilder.buildOne("Spent")) + cellRecipeBuilder.build(spents),
                    listOf(incomeHeaderRecipeBuilder.buildOne(Pair("Income",it.second))) + incomeRecipeBuilder.build(incomes),
                    listOf(headerRecipeBuilder.buildOne("Budgeted")) + cellRecipeBuilder.build(budgeteds)
                ).reflectXY()
            )
        }
    }
}
