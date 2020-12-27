package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.tmcommonkotlin.misc.GenericRecyclerViewAdapter
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import kotlinx.android.synthetic.main.frag_categorize.*
import kotlinx.android.synthetic.main.item_category_btn.view.*

class CategorizeFrag : Fragment(R.layout.frag_categorize), GenericRecyclerViewAdapter.Callbacks {
    val app by lazy { requireActivity().application as App }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(app.appComponent.getRepo()) } }
    val categorizeVM: CategorizeVM by viewModels { createVmFactory { CategorizeVM(app.appComponent.getRepo(), transactionsVM) }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
    }

    private fun setupBinds() {
        textview_date.bindIncoming(categorizeVM.dateAsString)
        textview_amount.bindIncoming(categorizeVM.transactionBox) { it.first?.amount?.toString()?:"" }
        textview_description.bindIncoming(categorizeVM.transactionBox) { it.first?.description?:"" }
        textview_amount_left.bindIncoming(transactionsVM.uncategorizedSpendsSize)
    }

    private fun setupViews() {
        // recyclerview
        recyclerview_categories.layoutManager = GridLayoutManager(requireActivity(), 3,
            GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = GenericRecyclerViewAdapter(requireActivity(), this, R.layout.item_category_btn)
    }

    override fun bindRecyclerItem(holder: GenericRecyclerViewAdapter.ViewHolder, view: View) {
        view.btn_category.text = categoriesAppVM.choosableCategories.value[holder.adapterPosition].name
        view.btn_category.setOnClickListener {
            categorizeVM.setTransactionCategory(categoriesAppVM.choosableCategories.value[holder.adapterPosition])
        }
    }

    override fun getRecyclerDataSize(): Int {
        return categoriesAppVM.choosableCategories.value.size
    }
}