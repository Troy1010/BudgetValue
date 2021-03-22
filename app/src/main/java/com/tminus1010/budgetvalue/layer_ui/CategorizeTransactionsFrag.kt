package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.middleware.GenViewHolder2
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.viewBinding
import com.tminus1010.budgetvalue.modules_shared.IViewModels
import com.tminus1010.budgetvalue.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.format.DateTimeFormatter

class CategorizeTransactionsFrag : Fragment(R.layout.frag_categorize), IViewModels {
    val binding by viewBinding(FragCategorizeBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        binding.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, true)
        binding.recyclerviewCategories.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                holder.binding.btnCategory.apply {
                    text = domain.userCategories.value[holder.adapterPosition].name
                    setOnClickListener { categorizeVM.finishTransactionWithCategory(domain.userCategories.value[holder.adapterPosition]) }
                }
            }

            override fun getItemCount() = domain.userCategories.value.size
        }
        // # Clicks
        binding.btnAdvanced.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_advancedCategorizeFrag) }
        binding.btnDeleteCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_categoryCustomizationFrag) }
        binding.btnNewCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_newCategoryFrag) }
        //
        binding.textviewDate.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        binding.textviewAmount.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        binding.textviewDescription.bindIncoming(categorizeVM.transactionBox)
        { it.unbox?.description ?: "" }
        binding.textviewAmountLeft.bindIncoming(transactionsVM.uncategorizedSpendsSize)
        categorizeVM.hasUncategorizedTransaction
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { binding.btnAdvanced.isEnabled = it }
    }
}