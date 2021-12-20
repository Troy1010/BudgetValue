package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.framework.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.databinding.SubfragChooseCategoryBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseCategoryVM
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCategorySubFrag : Fragment(R.layout.subfrag_choose_category) {
    lateinit var vb: SubfragChooseCategoryBinding
    val chooseCategoryVM by viewModels<ChooseCategoryVM>() // Make a ChooseCategoryVM
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragChooseCategoryBinding.bind(view)
        // # Setup View
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        // # Bind Presentation State
        vb.recyclerviewCategories.bind(chooseCategoryVM.categoryButtonVMItems) { categories ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder2(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun getItemCount() = categories.size
                override fun onLifecycleAttached(holder: GenViewHolder2<ItemCategoryBtnBinding>) {
                    categories[holder.adapterPosition].bind(holder.vb.btnCategory)
                }
            }
        }
    }
}