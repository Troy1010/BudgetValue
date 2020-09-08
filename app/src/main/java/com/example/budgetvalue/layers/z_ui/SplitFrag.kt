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
import com.example.budgetvalue.layers.z_ui.TMTableView.CellRecipe
import com.example.budgetvalue.layers.z_ui.TMTableView.CellRecipeBuilder
import com.example.budgetvalue.layers.z_ui.TMTableView.CellRecipeBuilder.Default
import com.example.budgetvalue.util.combineLatestAsTuple
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_split.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*

class SplitFrag : Fragment(R.layout.frag_split) {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val categoriesVM: CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val accountsVM: AccountsVM by activityViewModels{ vmFactoryFactory { AccountsVM(appComponent.getRepo()) }}
    val splitVM: SplitVM by activityViewModels { vmFactoryFactory { SplitVM(appComponent.getRepo(), categoriesVM, transactionsVM.transactions, accountsVM.accounts ) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        val cellRecipeBuilder = CellRecipeBuilder(requireContext())
        val headerRecipeBuilder = CellRecipeBuilder(requireContext(), Default.HEADER)
        combineLatestAsTuple(
            splitVM.rowDatas,
            splitVM.incomeTotal
        ).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(viewLifecycleOwner).subscribe {
            val rowDatas = it.first
            myTableView_1.setData(
                arrayListOf(
                    headerRecipeBuilder.build(listOf("Category","Spent")) +
                            CellRecipe({ View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
                                Pair("Income", it.second),
                                {v, d ->
                                    v.textview_header.text = d.first
                                    v.textview_number.text = d.second.toString()
                                }
                            ) +
                            headerRecipeBuilder.build("Budgeted")
                ) + rowDatas.map { cellRecipeBuilder.build( it.toListStr()) }
            )
        }
    }
}
// TODO weird bug if only 3 headers
