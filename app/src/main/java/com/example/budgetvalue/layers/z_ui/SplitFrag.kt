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
import com.example.budgetvalue.layers.z_ui.TMTableView.TableViewColumnData
import com.example.budgetvalue.util.combineLatestAsTuple
import com.example.budgetvalue.util.sortByList
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_split.*
import java.util.*
import kotlin.collections.HashMap

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
            splitVM.budgetedCategoryAmounts
        ).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(viewLifecycleOwner).subscribe {
            val activeCategories = it.first
            myTableView_1.setData(listOf(
                TableViewColumnData.createDAsString(requireContext(), "Category", it.first.map { it.name }),
                TableViewColumnData.createDAsString(requireContext(), "Spent", it.second.sortByList(activeCategories).map { it.value }),
                TableViewColumnData.createDAsString(requireContext(), "Income", it.third.sortByList(activeCategories).map { it.value }),
                TableViewColumnData.createDAsString(requireContext(), "Budgeted", it.fourth.sortByList(activeCategories).map { it.value })
            ))
        }
    }
}
