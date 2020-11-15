package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import com.tminus1010.tmcommonkotlin.misc.GenericRecyclerViewAdapter
import com.tminus1010.tmcommonkotlin.misc.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_categorize_spends.*
import kotlinx.android.synthetic.main.item_category_btn.view.*

class CategorizeFrag : Fragment(R.layout.frag_categorize_spends), GenericRecyclerViewAdapter.Callbacks {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val categorizeVM: CategorizeVM by viewModels { vmFactoryFactory { CategorizeVM(appComponent.getRepo(), transactionsVM) }}
    val categoriesVM: CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
    }

    private fun setupBinds() {
        textview_date.rxBindOneWay(categorizeVM.dateAsString)
        textview_amount.rxBindOneWay(categorizeVM.transaction) { it.amount.toString() }
        tv_description.rxBindOneWay(categorizeVM.transaction) { it.description }
        textview_amount_left.rxBindOneWay(transactionsVM.uncategorizedSpendsSize)
    }

    private fun setupViews() {
        // recyclerview
        recyclerview_categories.layoutManager = GridLayoutManager(requireActivity(), 3,
            GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = GenericRecyclerViewAdapter(this, requireActivity(), R.layout.item_category_btn)
    }

    override fun bindRecyclerItemView(view: View, i: Int) {
        view.btn_category.text = categoriesVM.choosableCategories.value[i].name
        view.btn_category.setOnClickListener {
            categorizeVM.setTransactionCategory(categoriesVM.choosableCategories.value[i])
        }
    }

    override fun getRecyclerDataSize(): Int {
        return categoriesVM.choosableCategories.value.size
    }
}