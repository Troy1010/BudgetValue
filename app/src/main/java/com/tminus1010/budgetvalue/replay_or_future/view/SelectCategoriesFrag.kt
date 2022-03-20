package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue.all_features.framework.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragSelectCategoriesBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.replay_or_future.presentation.SelectCategoriesVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
@Deprecated("No longer using")
class SelectCategoriesFrag : Fragment(R.layout.frag_select_categories) {
    private val vb by viewBinding(FragSelectCategoriesBinding::bind)
    private val selectCategoriesVM by viewModels<SelectCategoriesVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        // # Events
        selectCategoriesVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.buttonsview.bind(selectCategoriesVM.buttons) { buttons = it }
        vb.recyclerviewCategories.bind(selectCategoriesVM.categoryButtonVMItems.map { it.map { it.toViewItemRecipe(requireContext()) } }) { viewItemRecipes ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder2(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun getItemCount() = viewItemRecipes.size
                override fun onLifecycleAttached(holder: GenViewHolder2<ItemCategoryBtnBinding>) {
                    viewItemRecipes[holder.adapterPosition].bind(holder.vb)
                }
            }
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFuture2Frag)
        }
    }
}