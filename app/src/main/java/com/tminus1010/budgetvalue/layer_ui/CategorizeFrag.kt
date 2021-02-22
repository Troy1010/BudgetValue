package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.GenViewHolder
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.extensions.nav
import com.tminus1010.budgetvalue.extensions.v
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.unbox
import com.tminus1010.tmcommonkotlin_rx.observe
import kotlinx.android.synthetic.main.frag_categorize.*
import kotlinx.android.synthetic.main.frag_categorize.view.*
import kotlinx.android.synthetic.main.item_category_btn.view.*
import java.time.format.DateTimeFormatter

class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels2 {
        TransactionsVM(app.appComponent.getRepo(),
            app.appComponent.getDatePeriodGetter())
    }
    val categorizeVM: CategorizeVM by viewModels2 {
        CategorizeVM(app.appComponent.getRepo(),
            transactionsVM)
    }
    val advancedCategorizeVM by activityViewModels2 { AdvancedCategorizeVM(categorizeVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        recyclerview_categories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = object : RecyclerView.Adapter<GenViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LayoutInflater.from(requireActivity())
                    .inflate(R.layout.item_category_btn, parent, false)
                    .let { GenViewHolder(it) }

            override fun onBindViewHolder(holder: GenViewHolder, position: Int) {
                holder.itemView.btn_category.apply {
                    text = repo.activeCategories.value[holder.adapterPosition].name
                    setOnClickListener { categorizeVM.finishTransactionWithCategory(repo.activeCategories.value[holder.adapterPosition]) }
                }
            }

            override fun getItemCount() = repo.activeCategories.value.size
        }
        // # Views
        textview_date.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        textview_amount.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        textview_description.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.description ?: "" }
        textview_amount_left.bindIncoming(transactionsVM.uncategorizedSpendsSize)
        v.btn_advanced.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_advancedCategorizeFrag) }
        v.btn_delete_category.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_categoryCustomizationFrag) }
        v.btn_new_category.setOnClickListener {  }
        categorizeVM.hasUncategorizedTransaction.observe(viewLifecycleOwner) { btn_advanced.isEnabled = it }
    }
}