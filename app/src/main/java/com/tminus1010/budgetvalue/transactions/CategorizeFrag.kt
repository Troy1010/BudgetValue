package com.tminus1010.budgetvalue.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    val categorizeTransactionsVM by activityViewModels<CategorizeTransactionsVM>()
    val categoriesVM by activityViewModels<CategoriesVM>()
    val transactionsVM by activityViewModels<TransactionsVM>()
    val vb by viewBinding(FragCategorizeBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # RecyclerView
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(3, 15))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, true)
        vb.recyclerviewCategories.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                holder.vb.btnCategory.apply {
                    text = categoriesVM.userCategories.value[holder.adapterPosition].name
                    setOnClickListener { categorizeTransactionsVM.finishTransactionWithCategory(categoriesVM.userCategories.value[holder.adapterPosition]) }
                }
            }

            override fun getItemCount() = categoriesVM.userCategories.value.size
        }
        // # Clicks
        vb.btnAdvanced.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_advancedCategorizeFrag) }
        vb.btnDeleteCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_categoryCustomizationFrag) }
        vb.btnNewCategory.setOnClickListener { nav.navigate(R.id.action_categorizeFrag_to_newCategoryFrag) }
        //
        vb.textviewDate.bindIncoming(categorizeTransactionsVM.transactionBox)
        { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        vb.textviewAmount.bindIncoming(categorizeTransactionsVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        vb.textviewDescription.bindIncoming(categorizeTransactionsVM.transactionBox)
        { it.unbox?.description ?: "" }
        vb.textviewAmountLeft.bindIncoming(transactionsVM.uncategorizedSpendsSize)
        categorizeTransactionsVM.hasUncategorizedTransaction
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { vb.btnAdvanced.isEnabled = it }
    }
}