package com.tminus1010.buva.ui.choose_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.ItemCategoryBtnBinding
import com.tminus1010.buva.databinding.SubfragChooseCategoryBinding
import com.tminus1010.buva.framework.android.GridMarginDecoration
import com.tminus1010.buva.framework.android.LifecycleRVAdapter2
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCategorySubFrag : Fragment(R.layout.subfrag_choose_category) {
    lateinit var vb: SubfragChooseCategoryBinding
    val viewModel by viewModels<ChooseCategoryVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragChooseCategoryBinding.bind(view)
        // # Events
        viewModel.navUp.observe(this) { parentFragmentManager.popBackStack() }
        // # State
        vb.textviewPartialAmount.bind(viewModel.partialAmountToCategorize) { text = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(GridMarginDecoration(spanSize, 7, false))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(viewModel.categoryButtonVMItems) { buttonVMItems ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun getItemCount() = buttonVMItems.size
                override fun onLifecycleAttached(holder: GenViewHolder<ItemCategoryBtnBinding>) {
                    buttonVMItems[holder.adapterPosition].bind(holder.vb.btnCategory)
                }
            }
        }
    }
}