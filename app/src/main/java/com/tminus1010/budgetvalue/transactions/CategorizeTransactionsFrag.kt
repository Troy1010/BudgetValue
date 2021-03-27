package com.tminus1010.budgetvalue.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.aa_core.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.aa_core.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.aa_core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue.aa_core.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue.aa_core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.aa_core.middleware.unbox
import com.tminus1010.budgetvalue.aa_shared.ui.IViewModels
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.format.DateTimeFormatter

class CategorizeTransactionsFrag : Fragment(R.layout.frag_categorize), IViewModels {
    val vb by viewBinding(FragCategorizeBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, true)
        vb.recyclerviewCategories.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                holder.vb.btnCategory.apply {
                    text = categoriesVM.userCategories.value[holder.adapterPosition].name
                    setOnClickListener { categorizeVM.finishTransactionWithCategory(categoriesVM.userCategories.value[holder.adapterPosition]) }
                }
            }

            override fun getItemCount() = categoriesVM.userCategories.value.size
        }
        // # Clicks
        vb.btnAdvanced.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_advancedCategorizeFrag) }
        vb.btnDeleteCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_categoryCustomizationFrag) }
        vb.btnNewCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_newCategoryFrag) }
        //
        vb.textviewDate.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        vb.textviewAmount.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        vb.textviewDescription.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.description ?: "" }
        vb.textviewAmountLeft.bindIncoming(transactionsVM.uncategorizedSpendsSize)
        categorizeVM.hasUncategorizedTransaction
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { vb.btnAdvanced.isEnabled = it }
    }
}