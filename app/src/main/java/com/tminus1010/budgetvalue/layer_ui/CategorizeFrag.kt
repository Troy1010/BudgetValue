package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.GenViewHolder
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.IViewModelFactories
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.repo
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import kotlinx.android.synthetic.main.frag_categorize.*
import kotlinx.android.synthetic.main.item_category_btn.view.*
import java.time.format.DateTimeFormatter

class CategorizeFrag : Fragment(R.layout.frag_categorize), IViewModelFactories {
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
        // # Clicks
        btn_advanced.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_advancedCategorizeFrag) }
        btn_delete_category.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_categoryCustomizationFrag) }
        btn_new_category.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_newCategoryFrag) }
        //
        textview_date.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        textview_amount.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        textview_description.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.description ?: "" }
        textview_amount_left.bindIncoming(transactionsVM.uncategorizedSpendsSize)
        categorizeVM.hasUncategorizedTransaction.observe(viewLifecycleOwner) { btn_advanced.isEnabled = it }
    }

    override val viewModelFactoriesHelper by lazy { ViewModelFactoriesHelper(requireActivity(), appComponent) }
}