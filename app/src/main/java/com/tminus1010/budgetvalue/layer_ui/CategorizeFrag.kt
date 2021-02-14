package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.GenericRecyclerViewAdapter6
import com.tminus1010.budgetvalue.GenViewHolder
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import kotlinx.android.synthetic.main.frag_categorize.*
import kotlinx.android.synthetic.main.item_category_btn.view.*

class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    val app by lazy { requireActivity().application as App }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val transactionsVM: TransactionsVM by activityViewModels2 { TransactionsVM(app.appComponent.getRepo(), app.appComponent.getDatePeriodGetter()) }
    val categorizeVM: CategorizeVM by viewModels2 { CategorizeVM(app.appComponent.getRepo(), transactionsVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
    }

    private fun setupViews() {
        recyclerview_categories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = object : RecyclerView.Adapter<GenViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LayoutInflater.from(requireActivity())
                    .inflate(R.layout.item_category_btn, parent, false)
                    .let { GenViewHolder(it) }

            override fun onBindViewHolder(holder: GenViewHolder, position: Int) {
                holder.itemView.btn_category.apply {
                    text = categoriesAppVM.choosableCategories.value[holder.adapterPosition].name
                    setOnClickListener { categorizeVM.setTransactionCategory(categoriesAppVM.choosableCategories.value[holder.adapterPosition]) }
                }
            }

            override fun getItemCount() = categoriesAppVM.choosableCategories.value.size
        }
    }

    private fun setupBinds() {
        textview_date.bindIncoming(categorizeVM.dateAsString)
        textview_amount.bindIncoming(categorizeVM.transactionBox) {
            it.first?.amount?.toString() ?: ""
        }
        textview_description.bindIncoming(categorizeVM.transactionBox) {
            it.first?.description ?: ""
        }
        textview_amount_left.bindIncoming(transactionsVM.uncategorizedSpendsSize)
    }
}