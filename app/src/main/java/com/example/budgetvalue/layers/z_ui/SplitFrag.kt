package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.layers.view_models.AccountsVM
import com.example.budgetvalue.layers.view_models.SplitVM
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.budgetvalue.layers.z_ui.TMTableView.CellData
import com.example.budgetvalue.layers.z_ui.TMTableView.CellDataCollection
import com.example.budgetvalue.util.combineLatestAsTuple
import com.example.budgetvalue.util.generateLipsum
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_split.*

class SplitFrag : Fragment(R.layout.frag_split) {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val categoriesVM: CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val accountsVM: AccountsVM by activityViewModels{ vmFactoryFactory { AccountsVM(appComponent.getRepo()) }}
    val splitVM: SplitVM by activityViewModels { vmFactoryFactory { SplitVM(appComponent.getRepo(), categoriesVM, transactionsVM.transactions, accountsVM.accounts ) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logz("onViewCreated")
        setupObservers()
    }

    private fun setupObservers() {
        combineLatestAsTuple(
            categoriesVM.categories,
            splitVM.spentCategoryAmounts,
            splitVM.incomeCategoryAmounts,
            splitVM.budgetedCategoryAmounts,
            splitVM.incomeTotal
        ).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(viewLifecycleOwner).subscribe {
            val activeCategories = it.first
            myTableView_1.setDataByColumn(
                listOf(
                    CellDataCollection.create(requireContext(), it.first.map { it.name }).toCellDatas().also { it.add(0, CellData.create2(requireContext(), "Category")) },
//                    CellDataCollection.create(requireContext(), it.first.map { it.name }).toCellDatas().also { it.add(0, CellData.create2(requireContext(), "Category")) },
                    CellDataCollection.create(requireContext(), generateLipsum(2)).toCellDatas().also { it.add(0, CellData.create2(requireContext(), "Spent")) },
                    CellDataCollection.create(requireContext(), generateLipsum(3)).toCellDatas().also { it.add(0, CellData.create2(requireContext(), "Income")) },
                    CellDataCollection.create(requireContext(), generateLipsum(4)).toCellDatas().also { it.add(0, CellData.create2(requireContext(), "Budgeted")) }
                )
//                listOf(
//                    ColumnData.createCastString(requireContext(), "Category", it.first.map { it.name }),
//                    ColumnData.createCastString(requireContext(), "Spent", it.second.sortByList(activeCategories).map { it.value }),
//                    ColumnData.createUniqueHeader(requireContext(), TableViewCellData(
//                        { View.inflate(context, R.layout.tableview_header_income, null) },
//                        { view, any ->
//                            view as LinearLayout
//                            any as Pair<String, String>
//                            view.textview_header.text = any.first
//                            view.textview_number.text = any.second
//                        },
//                        Pair("Income", it.fifth.toString())
//                    ), it.third.sortByList(activeCategories).map { it.value.toString() }),
//                    ColumnData.createCastString(requireContext(), "Budgeted", it.fourth.sortByList(activeCategories).map { it.value })
//                ) as List<TableViewCellData>
            )
        }
    }
}
