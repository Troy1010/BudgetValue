package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.globals.appComponent
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import com.tminus1010.tmcommonkotlin.misc.GenericRecyclerViewAdapter
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import kotlinx.android.synthetic.main.frag_actual.*
import kotlinx.android.synthetic.main.item_category_btn.view.*

class ActualFrag : Fragment(R.layout.frag_actual), GenericRecyclerViewAdapter.Callbacks {
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(appComponent.getRepo()) } }
    val actualVM: ActualVM by viewModels { createVmFactory { ActualVM(appComponent.getRepo(), transactionsVM) }}
    val categoriesVM: CategoriesVM by activityViewModels { createVmFactory { CategoriesVM() } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
    }

    private fun setupBinds() {
        textview_date.rxBindOneWay(actualVM.dateAsString)
        textview_amount.rxBindOneWay(actualVM.transactionBox) { it.first?.amount?.toString()?:"" }
        tv_description.rxBindOneWay(actualVM.transactionBox) { it.first?.description?:"" }
        textview_amount_left.rxBindOneWay(transactionsVM.uncategorizedSpendsSize)
    }

    private fun setupViews() {
        // recyclerview
        recyclerview_categories.layoutManager = GridLayoutManager(requireActivity(), 3,
            GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = GenericRecyclerViewAdapter(requireActivity(), this, R.layout.item_category_btn)
    }

    override fun bindRecyclerItem(holder: GenericRecyclerViewAdapter.ViewHolder, view: View) {
        view.btn_category.text = categoriesVM.choosableCategories.value[holder.adapterPosition].name
        view.btn_category.setOnClickListener {
            actualVM.setTransactionCategory(categoriesVM.choosableCategories.value[holder.adapterPosition])
        }
    }

    override fun getRecyclerDataSize(): Int {
        return categoriesVM.choosableCategories.value.size
    }
}