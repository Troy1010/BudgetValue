package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.databinding.FragSplitBinding
import com.example.budgetvalue.layers.view_models.AccountsVM
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.layers.view_models.SplitVM
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.budgetvalue.layers.z_ui.table_view.MyTableViewAdapter
import com.example.budgetvalue.layers.z_ui.table_view.models.CellModel
import com.example.budgetvalue.layers.z_ui.table_view.models.ColumnHeaderModel
import com.example.budgetvalue.layers.z_ui.table_view.models.RowHeaderModel
import com.example.budgetvalue.util.Quintuple
import com.example.budgetvalue.util.combineLatestAsTuple
import com.example.budgetvalue.util.toLiveData2
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_split.*
import java.math.BigDecimal

class SplitFrag : Fragment() {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val accountsVM: AccountsVM by activityViewModels { vmFactoryFactory { AccountsVM(appComponent.getRepo()) } }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val categoriesVM: CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }
    val splitVM: SplitVM by viewModels { vmFactoryFactory { SplitVM(appComponent.getRepo(), categoriesVM, transactionsVM.transactions, accountsVM.accounts) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mBinding: FragSplitBinding =
            DataBindingUtil.inflate(inflater, R.layout.frag_split, container, false)
        mBinding.lifecycleOwner = this
        mBinding.splitVM = splitVM
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tableViewAdapter = MyTableViewAdapter(requireContext())
        tableview_1.setAdapter(tableViewAdapter)
        combineLatestAsTuple(
            splitVM.incomeTotal,
            splitVM.activeCategories,
            splitVM.spentCategoryAmounts,
            splitVM.incomeCategoryAmounts,
            splitVM.budgetedCategoryAmounts
        ).bindToLifecycle(viewLifecycleOwner).observeOn(AndroidSchedulers.mainThread()).subscribe { quintuple ->
            tableViewAdapter.setAllItems(
                listOf(
                    ColumnHeaderModel("Spent"),
                    ColumnHeaderModel("Income", quintuple.first),
                    ColumnHeaderModel("Budgeted")
                ),
                quintuple.second.map { RowHeaderModel(it.name) },
                (quintuple.second.indices).map {
                    listOf(
                        CellModel(quintuple.third[it]),
                        CellModel(quintuple.fourth.getOrNull(it) ?: BigDecimal.ZERO),
                        CellModel(quintuple.fifth[it])
                    )
                }
            )
        }
    }
}